package com.digiventure.ventnote.feature.note_creation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.digiventure.ventnote.commons.richtext.RichTextState
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.TagModel

class NoteCreationPageMockVM: ViewModel(), NoteCreationPageBaseVM {
    override val loader: MutableLiveData<Boolean> = MutableLiveData(false)
    override val titleText: MutableState<String> = mutableStateOf("")
    override val descriptionText: MutableState<String> = mutableStateOf("")
    override val titleRichTextState: RichTextState = RichTextState()
    override val richTextState: RichTextState = RichTextState()

    override val allTags: LiveData<Result<List<TagModel>>> = MutableLiveData(
        Result.success(
            listOf(
                TagModel(1, "Work", "#EF5350"),
                TagModel(2, "Personal", "#42A5F5")
            )
        )
    )
    override val selectedTagIds: MutableState<Set<Int>> = mutableStateOf(emptySet())

    override suspend fun addNote(note: NoteModel): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun setTagsForNote(noteId: Int, tagIds: List<Int>): Result<Boolean> =
        Result.success(true)
}