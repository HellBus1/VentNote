package com.digiventure.ventnote.feature.note_detail.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.digiventure.ventnote.commons.richtext.RichTextState
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.TagModel

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
     * State for handling title & description TextField
     * */
    var titleText: MutableState<String>
    var descriptionText: MutableState<String>

    /**
     * Rich text state for the title and body editors
     * */
    val titleRichTextState: RichTextState
    val richTextState: RichTextState

    /**
     * State for handling isEditing
     * */
    var isEditing: MutableState<Boolean>

    /**
     * retrieve responsible note by it's id
     * @param id is a note id passed from NoteList,
     * */
    suspend fun getNoteDetail(id: Int)

    /**
     * update single note
     * @param note is a note model
     * */
    suspend fun updateNote(note: NoteModel): Result<Boolean>

    /**
     * delete NoteList
     * @param notes is a list of note
     */
    suspend fun deleteNoteList(vararg notes: NoteModel): Result<Boolean>

    /**
     * All available tags in the system.
     */
    val allTags: LiveData<Result<List<TagModel>>>

    /**
     * Tags currently assigned to the note being viewed/edited.
     */
    val noteTags: LiveData<Result<List<TagModel>>>

    /**
     * Set of tag IDs selected for this note (editable, max 3).
     */
    val selectedTagIds: MutableState<Set<Int>>

    /**
     * Load the tags for the given note.
     */
    suspend fun loadTagsForNote(noteId: Int)

    /**
     * Save the selected tags to the note.
     */
    suspend fun setTagsForNote(noteId: Int, tagIds: List<Int>): Result<Boolean>
}