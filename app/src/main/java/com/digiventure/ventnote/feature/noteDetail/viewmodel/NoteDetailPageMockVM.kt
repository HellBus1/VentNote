package com.digiventure.ventnote.feature.noteDetail.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.digiventure.ventnote.data.local.NoteModel


class NoteDetailPageMockVM: ViewModel(), NoteDetailPageBaseVM {
    override var noteDetail: MutableLiveData<Result<NoteModel>> = MutableLiveData()

    override var titleText: MutableState<String> = mutableStateOf("This is sample title text")
    override var descriptionText: MutableState<String> = mutableStateOf("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus pretium odio maximus tellus pellentesque, a dignissim massa commodo.\n")
    override var isEditing: MutableState<Boolean> = mutableStateOf(false)

    override suspend fun getNoteDetail(id: Int) {}
}