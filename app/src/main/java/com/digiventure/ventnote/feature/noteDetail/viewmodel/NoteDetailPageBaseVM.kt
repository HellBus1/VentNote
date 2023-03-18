package com.digiventure.ventnote.feature.noteDetail.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.MutableLiveData
import com.digiventure.ventnote.data.local.NoteModel

interface NoteDetailPageBaseVM {
    /**
     * Contain note detail
     * */
    var noteDetail: MutableLiveData<Result<NoteModel>>

    /**
     * State for handling title & description textfield
     * */
    var titleText: MutableState<String>
    var descriptionText: MutableState<String>

    /**
     * State for handling isEditing
     * */
    var isEditing: MutableState<Boolean>

    /**
     * @param id is a note id passed from notelist,
     * retrieve responsible note by it's id
     * */
    suspend fun getNoteDetail(id: Int)
}