package com.digiventure.ventnote.feature.note_detail.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.digiventure.ventnote.commons.richtext.RichTextState
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.TagModel


class NoteDetailPageMockVM: ViewModel(), NoteDetailPageBaseVM {
    override val loader: MutableLiveData<Boolean> = MutableLiveData(false)
    override var noteDetail: MutableLiveData<Result<NoteModel>> = MutableLiveData()

    override var titleText: MutableState<String> = mutableStateOf("This is sample title text")
    override var descriptionText: MutableState<String> = mutableStateOf("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus pretium odio maximus tellus pellentesque, a dignissim massa commodo.\n")
    override val titleRichTextState: RichTextState = RichTextState()
    override val richTextState: RichTextState = RichTextState()
    override var isEditing: MutableState<Boolean> = mutableStateOf(false)

    // Tags
    override val allTags: LiveData<Result<List<TagModel>>> = MutableLiveData(
        Result.success(
            listOf(
                TagModel(1, "Work", "#EF5350"),
                TagModel(2, "Personal", "#42A5F5"),
                TagModel(3, "Ideas", "#66BB6A")
            )
        )
    )
    override val noteTags: LiveData<Result<List<TagModel>>> = MutableLiveData(
        Result.success(listOf(TagModel(1, "Work", "#EF5350")))
    )
    override val selectedTagIds: MutableState<Set<Int>> = mutableStateOf(setOf(1))

    init {
        val mockNote = NoteModel(
            id = 0,
            title = "This is sample title text",
            note = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec dignissim, sem sit amet consectetur ornare, lorem orci vulputate tortor, scelerisque vulputate elit nulla sed lacus.\n"
        )
        noteDetail.value = Result.success(mockNote)
    }

    override suspend fun getNoteDetail(id: Int) {}

    override suspend fun updateNote(note: NoteModel): Result<Boolean> = Result.success(true)
    override suspend fun deleteNoteList(vararg notes: NoteModel): Result<Boolean> = Result.success(true)
    override suspend fun loadTagsForNote(noteId: Int) {}
    override suspend fun setTagsForNote(noteId: Int, tagIds: List<Int>): Result<Boolean> = Result.success(true)
}