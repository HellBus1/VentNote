package com.digiventure.ventnote.feature.noteBackup.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

interface NoteBackupPageBaseVM {
    /**
     * Handle loading state
     * */
    val loader: MutableLiveData<Boolean>

    val signInClient: GoogleSignInClient

    val googleAccount: MutableLiveData<GoogleSignInAccount?>

    suspend fun backupDB(credential: GoogleAccountCredential): Result<Unit>

    suspend fun syncDB(credential: GoogleAccountCredential): Result<Unit>

    fun logout(): Result<Unit>
}