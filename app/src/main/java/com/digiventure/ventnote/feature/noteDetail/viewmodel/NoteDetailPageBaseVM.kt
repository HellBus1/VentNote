package com.digiventure.ventnote.feature.noteDetail.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.MutableLiveData
import com.digiventure.ventnote.data.local.NoteModel

interface NoteDetailPageBaseVM {
    /**
     * Handling loading state
     * */
    val loader: MutableLiveData<Boolean>

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
     * retrieve responsible note by it's id
     * @param id is a note id passed from notelist,
     * */
    suspend fun getNoteDetail(id: Int)

    /**
     * update single note
     * @param notes is a note model
     * */
    suspend fun updateNoteList(vararg notes: NoteModel): Result<Boolean>

    /**
     * delete notelist
     * @param notes is a list of note
     */
    suspend fun deleteNoteList(vararg notes: NoteModel): Result<Boolean>
}