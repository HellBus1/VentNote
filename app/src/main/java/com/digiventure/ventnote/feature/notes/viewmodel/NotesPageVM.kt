package com.digiventure.ventnote.feature.notes.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.data.local.NoteDataStore
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.NoteRepository
import com.digiventure.ventnote.data.persistence.TagModel
import com.digiventure.ventnote.data.persistence.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.combine
import androidx.compose.runtime.snapshotFlow
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NotesPageVM @Inject constructor(
    private val repository: NoteRepository,
    private val tagRepository: TagRepository,
    private val noteDataStore: NoteDataStore,
    private val databaseProxy: DatabaseProxy
): ViewModel(), NotesPageBaseVM {
    // Manages the visibility of the loading dialog during asynchronous operations
    override val loader = MutableLiveData<Boolean>()
    
    // Holds the current sorting criteria (e.g. By Title, By Date) and ordering (Ascending/Descending)
    override val sortAndOrderData: MutableLiveData<Pair<String, String>> = MutableLiveData(
        Pair(Constants.UPDATED_AT, Constants.DESCENDING)
    )

    private val defaultException = Exception("Unknown error")

    // The primary list of notes currently displayed on the screen
    override val noteList: LiveData<Result<List<NoteModel>>>
        get() = _noteList
    private val _noteList = MutableLiveData<Result<List<NoteModel>>>()

    // All available tags for the chip bar
    private val _allTags = MutableLiveData<Result<List<TagModel>>>()
    override val allTags: LiveData<Result<List<TagModel>>> = _allTags

    // Currently selected tag ID (null = show all)
    override val selectedTagId = mutableStateOf<Int?>(null)

    // Map of note ID to list of its tags
    private val _noteTagsMap = MutableLiveData<Map<Int, List<TagModel>>>(emptyMap())
    override val noteTagsMap: LiveData<Map<Int, List<TagModel>>> = _noteTagsMap

    // Updates the sorting and ordering preferences, triggering a reactive reload of the notes list
    override fun sortAndOrder(sortBy: String, orderBy: String) {
        sortAndOrderData.value = Pair(sortBy, orderBy)
    }

    // Holds the current search query entered by the user in the top app bar
    override val searchedTitleText = mutableStateOf("")

    // Tracks the current layout style of the notes list (e.g., List View vs. Staggered Grid View)
    override val noteViewMode = mutableStateOf(Constants.VIEW_MODE_LIST)

    init {
        viewModelScope.launch {
            noteDataStore.getStringData(Constants.NOTE_VIEW_MODE).collectLatest { mode ->
                if (mode.isNotEmpty()) {
                    noteViewMode.value = mode
                }
            }
        }
        // Observe tags for the chip bar
        viewModelScope.launch {
            tagRepository.getAllTags().collect { result ->
                _allTags.postValue(result)
            }
        }
        // Observe note-tag associations reactively
        viewModelScope.launch {
            combine(
                tagRepository.getAllTags(),
                databaseProxy.tagDao().getAllNoteTagCrossRefsFlow()
            ) { tagsResult, crossRefs ->
                val tags = tagsResult.getOrDefault(emptyList())
                crossRefs.groupBy { it.noteId }
                    .mapValues { entry ->
                        entry.value.mapNotNull { ref -> tags.find { it.id == ref.tagId } }
                    }
            }.collect { map ->
                _noteTagsMap.postValue(map)
            }
        }
    }

    // Updates the view mode state and persists it to the DataStore for future sessions
    override fun setNoteViewMode(mode: String) {
        noteViewMode.value = mode
        viewModelScope.launch {
            noteDataStore.setStringData(Constants.NOTE_VIEW_MODE, mode)
        }
    }

    // Indicates whether the UI is currently in "selection mode" (e.g., long-pressing notes to delete them)
    override val isMarking = mutableStateOf(false)
    // Maintains the list of notes currently selected by the user while in marking mode
    override val markedNoteList = mutableStateListOf<NoteModel>()

    // Selects all given notes, adding them to the marked list (preventing duplicates)
    override fun markAllNote(notes: List<NoteModel>) {
        markedNoteList.addAll(notes.minus((markedNoteList).toSet()))
    }

    // Clears the current selection without exiting marking mode
    override fun unMarkAllNote() {
        markedNoteList.clear()
    }

    // Toggles the selection state of a specific note
    override fun addToMarkedNoteList(note: NoteModel) {
        if (note in markedNoteList) {
            markedNoteList.remove(note)
        } else {
            markedNoteList.add(note)
        }
    }

    // Deletes the specified notes (or the currently marked notes if none are passed).
    // Automatically triggers a list refresh (observeNotes) upon successful deletion.
    override suspend fun deleteNoteList(vararg notes: NoteModel): Result<Boolean> =
        withContext(Dispatchers.IO) {
        loader.postValue(true)
        try {
            val items: List<NoteModel> = if (notes.isEmpty()) { markedNoteList } else { notes.toList() }
            repository.deleteNoteList(*items.toTypedArray()).onEach {
                loader.postValue(false)
                observeNotes()
            }.last()
        } catch (e: Exception) {
            loader.postValue(false)
            Result.failure(e)
        }
    }

    // Exits the marking mode and clears all current selections
    override fun closeMarkingEvent() {
        isMarking.value = false
        markedNoteList.clear()
    }

    // Sets up reactive observation of the database. Automatically fetches and updates 
    // the notes list whenever the sort order or selected tag filter changes.
    override fun observeNotes() {
        viewModelScope.launch {
            combine(
                sortAndOrderData.asFlow(),
                snapshotFlow { selectedTagId.value }
            ) { sortData, tagId ->
                Pair(sortData, tagId)
            }.collectLatest { (sortData, tagId) ->
                loader.postValue(true)
                val flow = if (tagId == null) {
                    repository.getNoteList(sortData.first, sortData.second)
                } else {
                    repository.getNotesByTag(tagId, sortData.first, sortData.second)
                }
                flow.onEach {
                    loader.postValue(false)
                }.collect { result ->
                    if (result.isSuccess) {
                        _noteList.postValue(result)
                    } else {
                        _noteList.postValue(Result.failure(
                            result.exceptionOrNull() ?: defaultException
                        ))
                    }
                }
            }
        }
    }

    // Updates the pinned state of a specific note, forcing it to the top of the list 
    // or unpinning it to return it to normal chronological/alphabetical order.
    override suspend fun toggleNotePin(noteId: Int, isPinned: Boolean): Result<Boolean> =
        withContext(Dispatchers.IO) {
            loader.postValue(true)
            try {
                repository.toggleNotePin(noteId, isPinned).onEach {
                    loader.postValue(false)
                }.last()
            } catch (e: Exception) {
                loader.postValue(false)
                Result.failure(e)
            }
        }
}
