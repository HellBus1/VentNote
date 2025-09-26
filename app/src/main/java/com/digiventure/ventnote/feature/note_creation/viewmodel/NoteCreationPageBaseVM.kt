package com.digiventure.ventnote.feature.note_creation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.MutableLiveData
import com.digiventure.ventnote.data.persistence.NoteModel

interface NoteCreationPageBaseVM {
    /**
     * Handling loading state
     * */
    val loader: MutableLiveData<Boolean>

    /**
     * State for handling title & description TextField
     * */
    val titleText: MutableState<String>
    val descriptionText: MutableState<String>
    val textFieldValue: MutableState<TextFieldValue>

    /**
     * create note
     * @param note is a note model
     * */
    suspend fun addNote(note: NoteModel): Result<Boolean>
    fun loadFromMarkdown(markdown: String)
    fun updateFormatTogglesBasedOnCursor()
    fun toggleItalic()
    fun toggleBold()
}