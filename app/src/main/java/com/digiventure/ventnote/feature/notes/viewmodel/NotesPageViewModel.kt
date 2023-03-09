package com.digiventure.ventnote.feature.notes.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.digiventure.ventnote.data.NoteRepository
import com.digiventure.ventnote.data.local.NoteModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesPageViewModel @Inject constructor(
    private val repository: NoteRepository
): ViewModel() {
    val noteList: LiveData<Result<List<NoteModel>>> = liveData {
        emitSource(repository.getNoteList()
            .onEach {}
            .asLiveData())
    }

    /**
     * 1. Toggle search field
     * 2. Searchfield value
     */
    val isSearching = mutableStateOf(false)
    val searchedTitleText = mutableStateOf("")

    /**
     * 1. Toggle marking action
     * 2. List of marked note
     * */
    val isMarking = mutableStateOf(false)
    val markedNoteList = mutableStateListOf<NoteModel>()

    fun markAllNote(notes: List<NoteModel>) {
        markedNoteList.addAll(notes.minus((markedNoteList).toSet()))
    }

    /**
     * Add data to markedNoteList, if exist remove instead
     * */
    fun addToMarkedNoteList(note: NoteModel) {
        if (note in markedNoteList) {
            markedNoteList.remove(note)
        } else {
            markedNoteList.add(note)
        }
    }

    fun unMarkAllNote() {
        markedNoteList.clear()
    }

    /**
     * Delete notes action
     */
    fun deleteNoteList(
        vararg notes: NoteModel,
        resultCallback: (Result<Boolean>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val items: List<NoteModel> = if (notes.isEmpty()) { markedNoteList } else { notes.toList() }
            repository.deleteNoteList(*items.toTypedArray()).collect {
                resultCallback(it)
            }
        }
    }
}

