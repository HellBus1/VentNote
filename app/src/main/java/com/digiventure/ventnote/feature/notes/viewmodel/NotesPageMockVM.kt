package com.digiventure.ventnote.feature.notes.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.TagModel

class NotesPageMockVM : ViewModel(), NotesPageBaseVM {
    override val loader = MutableLiveData<Boolean>(false)
    override val sortAndOrderData: MutableLiveData<Pair<String, String>> = MutableLiveData()

    override fun sortAndOrder(sortBy: String, orderBy: String) {}

    override val noteViewMode = mutableStateOf(com.digiventure.ventnote.commons.Constants.VIEW_MODE_LIST)
    override fun setNoteViewMode(mode: String) {
        noteViewMode.value = mode
    }

    override val noteList: LiveData<Result<List<NoteModel>>> =
        MutableLiveData(
            Result.success(
                listOf(
                    NoteModel(0, "Title 1", "Note 1"),
                    NoteModel(1, "Title 2", "Note 2"),
                    NoteModel(2, "Title 3", "Note 3"),
                    NoteModel(3, "Title 4", "Note 4")
                )
            )
        )

    override val allTags: LiveData<Result<List<TagModel>>> = MutableLiveData(
        Result.success(
            listOf(
                TagModel(1, "Work", "#EF5350"),
                TagModel(2, "Ideas", "#42A5F5")
            )
        )
    )

    override val selectedTagId = mutableStateOf<Int?>(null)

    override val noteTagsMap: LiveData<Map<Int, List<TagModel>>> = MutableLiveData(emptyMap())

    override val searchedTitleText = mutableStateOf("")

    override val isMarking = mutableStateOf(true)
    override val markedNoteList = mutableStateListOf<NoteModel>()

    override fun markAllNote(notes: List<NoteModel>) {}
    override fun unMarkAllNote() {}
    override fun addToMarkedNoteList(note: NoteModel) {}

    override suspend fun deleteNoteList(vararg notes: NoteModel): Result<Boolean> = Result.success(true)

    override fun closeMarkingEvent() {
        isMarking.value = false
        markedNoteList.clear()
    }

    override fun observeNotes() {}
}
