package com.digiventure.ventnote.feature.noteDetail.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.digiventure.ventnote.data.NoteRepository
import com.digiventure.ventnote.data.local.NoteModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NoteDetailPageVM @Inject constructor(
    private val repository: NoteRepository
): ViewModel(), NoteDetailPageBaseVM {
    override var noteDetail: MutableLiveData<Result<NoteModel>> = MutableLiveData()

    override var titleText: MutableState<String> = mutableStateOf("")
    override var descriptionText: MutableState<String> = mutableStateOf("")

    override var isEditing: MutableState<Boolean> = mutableStateOf(false)

    override suspend fun getNoteDetail(id: Int) = withContext(Dispatchers.IO) {
        repository.getNoteDetail(id).collect {
            noteDetail.postValue(it)
        }
    }
}