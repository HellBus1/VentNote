package com.digiventure.ventnote.feature.notes.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.digiventure.ventnote.data.NoteRepository
import com.digiventure.ventnote.data.local.NoteModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesPageViewModel @Inject constructor(
    private val repository: NoteRepository
): ViewModel() {
    val noteList: LiveData<Result<List<NoteModel>>> = liveData {
        emitSource(repository.getNoteList()
            .onEach {}
            .asLiveData())
    }

    /**
     * 1. Toggle search field
     * 2. Searchfield value
     */
    val isSearching = mutableStateOf(false)
    val searchedTitleText = mutableStateOf("")

    /**
     * 1. Toggle marking action
     * 2. List of marked note
     * */
    val isMarking = mutableStateOf(false)
    val markedNoteList = mutableStateListOf<NoteModel>()

    /**
     * Add data to markedNoteList, if exist remove instead
     * */
    fun addToMarkedNoteList(note: NoteModel) {
        if (note in markedNoteList) {
            markedNoteList.remove(note)
        } else {
            markedNoteList.add(note)
        }
    }

    fun markAllNote() {
        noteList.value?.getOrNull()?.forEach {
            if (it !in markedNoteList) {
                markedNoteList.add(it)
            }
        }
    }

    fun unMarkAllNote() {
        noteList.value?.getOrNull()?.forEach {
            if (it in markedNoteList) {
                markedNoteList.remove(it)
            }
        }
    }

    /**
     * Delete notes action
     */
    fun deleteNoteList(vararg notes: NoteModel) {
        viewModelScope.launch {
            val items: List<NoteModel> = if (notes.isEmpty()) { markedNoteList } else { notes.toList() }
            repository.deleteNoteList(*items.toTypedArray()).collect { }
        }
    }
}

