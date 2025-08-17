package com.digiventure.ventnote.feature.notes.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.digiventure.ventnote.data.persistence.NoteModel

interface NotesPageBaseVM {
    /**
     * Handle loading state
     * */
    val loader: MutableLiveData<Boolean>

    /**
     * Handle sorting and order data
     * */
    val sortAndOrderData: MutableLiveData<Pair<String, String>>

    /**
     *  Handle sorting and order
     * */
    fun sortAndOrder(sortBy: String, orderBy: String)

    /**
     * Handle NoteList state
     * */
    val noteList: LiveData<Result<List<NoteModel>>>

    /**
     * 1. Toggle search field
     * 2. SearchField value
     */
    val searchedTitleText: MutableState<String>

    /**
     * 1. Toggle marking action
     * 2. List of marked note
     * */
    val isMarking: MutableState<Boolean>
    val markedNoteList: SnapshotStateList<NoteModel>

    /**
     * Mark all note
     * @param notes is list of note that will be marked
     * */
    fun markAllNote(notes: List<NoteModel>)

    /**
     * Un-mark all note
     * */
    fun unMarkAllNote()

    /**
     * Mark or Un-mark a note
     * */
    fun addToMarkedNoteList(note: NoteModel)

    /**
     * Delete list of note
     * @param notes is vararg of note
     * */
    suspend fun deleteNoteList(vararg notes: NoteModel): Result<Boolean>

    /**
     * Close marking event
     * */
    fun closeMarkingEvent()

    fun observeNotes()
}