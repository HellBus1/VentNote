package com.digiventure.ventnote.feature.noteBackup.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

class NoteBackupPageMockVM: ViewModel(), NoteBackupPageBaseVM {
    override val loader = MutableLiveData<Boolean>()

    override val signInClient: GoogleSignInClient
        get() = TODO("Not yet implemented")

    override val googleAccount: MutableLiveData<GoogleSignInAccount?>
        get() = TODO("Not yet implemented")

    override suspend fun backupDB(credential: GoogleAccountCredential): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun syncDB(credential: GoogleAccountCredential): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun logout(): Result<Unit> {
        TODO("Not yet implemented")
    }
}