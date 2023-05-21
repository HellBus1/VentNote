package com.digiventure.ventnote.feature.note_backup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

class NoteBackupPageMockVM: ViewModel(), NoteBackupPageBaseVM {
    override val loader = MutableLiveData<Boolean>()

    override val signInClient: GoogleSignInClient?
        get() = null

    override val googleAccount: MutableLiveData<GoogleSignInAccount?>
        get() = MutableLiveData()
    override val savedDay: LiveData<Long>
        get() = liveData {  }
    override val maxAttempts: LiveData<Int>
        get() = liveData { emit(3) }

    override suspend fun backupDB(credential: GoogleAccountCredential): Result<Unit> = Result.success(Unit)

    override suspend fun syncDB(credential: GoogleAccountCredential): Result<Unit> = Result.success(Unit)

    override suspend fun setSavedDay(previousDay: Long, currentTime: Long): Result<Unit> = Result.success(Unit)

    override suspend fun setMaxSyncAttempt(value: Int): Result<Unit> = Result.success(Unit)

    override fun logout(): Result<Unit> = Result.success(Unit)
}