package com.digiventure.ventnote.feature.note_detail.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digiventure.ventnote.commons.richtext.RichTextState
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.NoteRepository
import com.digiventure.ventnote.data.persistence.TagModel
import com.digiventure.ventnote.data.persistence.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NoteDetailPageVM @Inject constructor(
    private val repository: NoteRepository,
    private val tagRepository: TagRepository
): ViewModel(), NoteDetailPageBaseVM {
    override val loader: MutableLiveData<Boolean> = MutableLiveData()
    override var noteDetail: MutableLiveData<Result<NoteModel>> = MutableLiveData()

    override var titleText: MutableState<String> = mutableStateOf("")
    override var descriptionText: MutableState<String> = mutableStateOf("")
    override val titleRichTextState: RichTextState = RichTextState()
    override val richTextState: RichTextState = RichTextState()

    override var isEditing: MutableState<Boolean> = mutableStateOf(false)

    // Tags
    private val _allTags = MutableLiveData<Result<List<TagModel>>>()
    override val allTags: LiveData<Result<List<TagModel>>> = _allTags

    private val _noteTags = MutableLiveData<Result<List<TagModel>>>()
    override val noteTags: LiveData<Result<List<TagModel>>> = _noteTags

    override val selectedTagIds: MutableState<Set<Int>> = mutableStateOf(emptySet())

    init {
        viewModelScope.launch {
            tagRepository.getAllTags().collect { result ->
                _allTags.postValue(result)
            }
        }
    }

    override suspend fun getNoteDetail(id: Int) = withContext(Dispatchers.IO) {
        loader.postValue(true)
        repository.getNoteDetail(id)
            .onEach { loader.postValue(false) }
            .collect {
                noteDetail.postValue(it)
            }
    }

    override suspend fun updateNote(note: NoteModel): Result<Boolean> = withContext(Dispatchers.IO) {
        loader.postValue(true)
        try {
            repository.updateNoteList(note).onEach {
                loader.postValue(false)
            }.last()
        } catch (e: Exception) {
            loader.postValue(false)
            Result.failure(e)
        }
    }

    override suspend fun deleteNoteList(vararg notes: NoteModel): Result<Boolean> = withContext(Dispatchers.IO) {
        loader.postValue(true)
        try {
            repository.deleteNoteList(*notes).onEach {
                loader.postValue(false)
            }.last()
        } catch (e: Exception) {
            loader.postValue(false)
            Result.failure(e)
        }
    }

    override suspend fun loadTagsForNote(noteId: Int) {
        withContext(Dispatchers.IO) {
            try {
                val result = tagRepository.getTagsForNote(noteId).first()
                _noteTags.postValue(result)
                result.getOrNull()?.let { tags ->
                    selectedTagIds.value = tags.map { it.id }.toSet()
                }
            } catch (e: Exception) {
                _noteTags.postValue(Result.failure(e))
            }
        }
    }

    override suspend fun setTagsForNote(noteId: Int, tagIds: List<Int>): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                tagRepository.setTagsForNote(noteId, tagIds).first()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}