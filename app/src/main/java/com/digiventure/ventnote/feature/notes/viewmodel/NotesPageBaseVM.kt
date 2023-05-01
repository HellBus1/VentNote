package com.digiventure.ventnote.feature.notes.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.digiventure.ventnote.data.local.NoteModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient

interface NotesPageBaseVM {
    /**
     * Handle loading state
     * */
    val loader: MutableLiveData<Boolean>

    /**
     *
     * */
    val noteList: LiveData<Result<List<NoteModel>>>

    /**
     * 1. Toggle search field
     * 2. Searchfield value
     */
    val isSearching: MutableState<Boolean>
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
     * Unmark all note
     * */
    fun unMarkAllNote()

    /**
     * Mark or unmark a note
     * */
    fun addToMarkedNoteList(note: NoteModel)

    /**
     * Delete list of note
     * @param notes is vararg of note
     * */
    suspend fun deleteNoteList(vararg notes: NoteModel): Result<Boolean>

    fun uploadDBtoDrive(): GoogleSignInClient
}