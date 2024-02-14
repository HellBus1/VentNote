package com.digiventure.ventnote.feature.notes.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.digiventure.ventnote.data.local.NoteModel

class NotesPageMockVM(
) : ViewModel(), NotesPageBaseVM {
    override val loader = MutableLiveData<Boolean>()
    override val sortAndOrderData: MutableLiveData<Pair<String, String>> = MutableLiveData()

    override fun sortAndOrder(sortBy: String, orderBy: String) {

    }

    override val noteList: LiveData<Result<List<NoteModel>>> = liveData {
        Result.success(
            listOf(
                NoteModel("", ""),
                NoteModel("", ""),
                NoteModel("", ""),
                NoteModel("", ""),
            )
        )
    }

    override val isSearching = mutableStateOf(false)
    override val searchedTitleText = mutableStateOf("")

    override val isMarking = mutableStateOf(false)
    override val markedNoteList = mutableStateListOf<NoteModel>()

    override fun markAllNote(notes: List<NoteModel>) {}

    override fun unMarkAllNote() {}

    override fun addToMarkedNoteList(note: NoteModel) {}

    override suspend fun deleteNoteList(vararg notes: NoteModel): Result<Boolean> = Result.success(true)

    override fun closeMarkingEvent() {
        isMarking.value = false
        markedNoteList.clear()
    }

    override fun closeSearchEvent() {
        isSearching.value = false
        searchedTitleText.value = ""
    }
}