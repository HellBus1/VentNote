package com.digiventure.ventnote.feature.notes.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.digiventure.ventnote.data.local.NoteModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

class NotesPageMockVM: ViewModel(), NotesPageBaseVM {
    override val loader = MutableLiveData<Boolean>()

    override val noteList: LiveData<Result<List<NoteModel>>> = liveData {}

    override val isSearching = mutableStateOf(false)
    override val searchedTitleText = mutableStateOf("")

    override val isMarking = mutableStateOf(false)
    override val markedNoteList = mutableStateListOf<NoteModel>()
    override val signInClient: GoogleSignInClient
        get() = TODO("Not yet implemented")

    override fun markAllNote(notes: List<NoteModel>) {}

    override fun unMarkAllNote() {}

    override fun addToMarkedNoteList(note: NoteModel) {}

    override suspend fun deleteNoteList(vararg notes: NoteModel): Result<Boolean> = Result.success(true)
    override suspend fun backupDB(credential: GoogleAccountCredential) {
        TODO("Not yet implemented")
    }
}