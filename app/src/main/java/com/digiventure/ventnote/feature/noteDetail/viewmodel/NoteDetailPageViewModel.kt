package com.digiventure.ventnote.feature.noteDetail.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digiventure.ventnote.data.NoteRepository
import com.digiventure.ventnote.data.local.NoteModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NoteDetailPageViewModel @Inject constructor(
    private val repository: NoteRepository
): ViewModel() {
    var noteDetail: MutableLiveData<Result<NoteModel>> = MutableLiveData()

    suspend fun getNoteDetail(id: Int) = withContext(Dispatchers.IO) {
        repository.getNoteDetail(id).collect {
            noteDetail.postValue(it)
        }
    }
}