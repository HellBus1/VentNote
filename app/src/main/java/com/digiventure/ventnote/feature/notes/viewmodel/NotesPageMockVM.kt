package com.digiventure.ventnote.feature.notes.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.digiventure.ventnote.data.persistence.NoteModel

class NotesPageMockVM : ViewModel(), NotesPageBaseVM {
    override val loader = MutableLiveData<Boolean>(false) // Initial value
    override val sortAndOrderData: MutableLiveData<Pair<String, String>> = MutableLiveData()

    override fun sortAndOrder(sortBy: String, orderBy: String) {
        // Mock implementation if needed
    }

    // More preview-friendly way to expose a list
    override val noteList: LiveData<Result<List<NoteModel>>> =
        MutableLiveData( // Use MutableLiveData and set its value directly
            Result.success(
                listOf(
                    NoteModel(0, "Title 1", "Note 1"),
                    NoteModel(1, "Title 2", "Note 2"),
                    NoteModel(2, "Title 3", "Note 3"),
                    NoteModel(3, "Title 4", "Note 4")
                )
            )
        )

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

    override fun observeNotes() {
        // In a real ViewModel, this might trigger the data loading.
        // For a mock, the data is already set.
    }
}
