package com.digiventure.ventnote.feature.note_creation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.digiventure.ventnote.data.persistence.NoteModel

class NoteCreationPageMockVM: ViewModel(), NoteCreationPageBaseVM {
    override val loader: MutableLiveData<Boolean> = MutableLiveData(false)
    override val titleText: MutableState<String> = mutableStateOf("")
    override val descriptionText: MutableState<String> = mutableStateOf("")

    override suspend fun addNote(note: NoteModel): Result<Boolean> {
        TODO("Not yet implemented")
    }
}