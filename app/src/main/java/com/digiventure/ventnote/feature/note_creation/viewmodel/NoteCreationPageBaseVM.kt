package com.digiventure.ventnote.feature.note_creation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.digiventure.ventnote.commons.richtext.RichTextState
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.TagModel

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

    /**
     * Rich text state for the title and body editors
     * */
    val titleRichTextState: RichTextState
    val richTextState: RichTextState

    /**
     * create note
     * @param note is a note model
     * */
    suspend fun addNote(note: NoteModel): Result<Boolean>

    /**
     * All available tags for the tag picker.
     */
    val allTags: LiveData<Result<List<TagModel>>>

    /**
     * Set of tag IDs currently selected for the note being created (max 3).
     */
    val selectedTagIds: MutableState<Set<Int>>

    /**
     * Associate a set of tags with the given note ID after creation.
     */
    suspend fun setTagsForNote(noteId: Int, tagIds: List<Int>): Result<Boolean>
}