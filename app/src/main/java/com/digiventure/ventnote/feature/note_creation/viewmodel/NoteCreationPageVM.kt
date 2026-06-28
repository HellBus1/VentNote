package com.digiventure.ventnote.feature.note_creation.viewmodel

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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NoteCreationPageVM @Inject constructor(
    private val repository: NoteRepository,
    private val tagRepository: TagRepository
): ViewModel(), NoteCreationPageBaseVM {
    override val loader: MutableLiveData<Boolean> = MutableLiveData()
    override val titleText: MutableState<String> = mutableStateOf("")
    override val descriptionText: MutableState<String> = mutableStateOf("")
    override val titleRichTextState: RichTextState = RichTextState()
    override val richTextState: RichTextState = RichTextState()

    // Tags
    private val _allTags = MutableLiveData<Result<List<TagModel>>>()
    override val allTags: LiveData<Result<List<TagModel>>> = _allTags
    override val selectedTagIds: MutableState<Set<Int>> = mutableStateOf(emptySet())

    init {
        viewModelScope.launch {
            tagRepository.getAllTags().collect { result ->
                _allTags.postValue(result)
            }
        }
    }

    override suspend fun addNote(note: NoteModel): Result<Boolean> = withContext(Dispatchers.IO) {
        loader.postValue(true)
        try {
            val result = repository.insertNote(note, selectedTagIds.value.toList()).first()
            loader.postValue(false)
            if (result.isSuccess) {
                Result.success(result.getOrNull() != -1L)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Insertion failed"))
            }
        } catch (e: Exception) {
            loader.postValue(false)
            Result.failure(e)
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