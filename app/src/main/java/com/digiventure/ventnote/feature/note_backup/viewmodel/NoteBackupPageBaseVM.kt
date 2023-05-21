package com.digiventure.ventnote.feature.note_backup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

interface NoteBackupPageBaseVM {
    /**
     * Handle loading state
     * */
    val loader: MutableLiveData<Boolean>

    val signInClient: GoogleSignInClient?

    val googleAccount: MutableLiveData<GoogleSignInAccount?>

    val savedDay: LiveData<Long>

    val maxAttempts: LiveData<Int>

    suspend fun backupDB(credential: GoogleAccountCredential): Result<Unit>

    suspend fun syncDB(credential: GoogleAccountCredential): Result<Unit>

    suspend fun setSavedDay(previousDay: Long, currentTime: Long): Result<Unit>

    suspend fun setMaxSyncAttempt(value: Int): Result<Unit>

    fun logout(): Result<Unit>
}