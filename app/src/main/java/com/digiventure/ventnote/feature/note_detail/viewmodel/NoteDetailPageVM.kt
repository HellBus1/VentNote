package com.digiventure.ventnote.feature.note_detail.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NoteDetailPageVM @Inject constructor(
    private val repository: NoteRepository
): ViewModel(), NoteDetailPageBaseVM {
    override val loader: MutableLiveData<Boolean> = MutableLiveData()
    override var noteDetail: MutableLiveData<Result<NoteModel>> = MutableLiveData()

    override var titleText: MutableState<String> = mutableStateOf("")
    override var descriptionText: MutableState<String> = mutableStateOf("")

    override var isEditing: MutableState<Boolean> = mutableStateOf(false)

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
}