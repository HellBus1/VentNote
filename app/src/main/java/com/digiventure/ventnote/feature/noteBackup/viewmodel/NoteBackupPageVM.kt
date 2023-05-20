package com.digiventure.ventnote.feature.noteBackup.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.digiventure.ventnote.data.NoteRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NoteBackupPageVM @Inject constructor(
    private val repository: NoteRepository,
    private val googleSignInClient: GoogleSignInClient,
): ViewModel(), NoteBackupPageBaseVM {
    override val loader = MutableLiveData<Boolean>()

    override val signInClient: GoogleSignInClient
        get() = googleSignInClient

    override val googleAccount: MutableLiveData<GoogleSignInAccount?> = MutableLiveData()

    override suspend fun backupDB(credential: GoogleAccountCredential): Result<Unit> =
        withContext(Dispatchers.IO) {
            loader.postValue(true)
            try {
                repository.uploadDBtoDrive(credential).onEach {
                    loader.postValue(false)
                }.last()
            } catch (e: Exception) {
                loader.postValue(false)
                Result.failure(e)
            }
    }

    override suspend fun syncDB(credential: GoogleAccountCredential): Result<Unit> =
        withContext(Dispatchers.IO) {
            loader.postValue(true)
            try {
                repository.syncDBFromDrive(credential).onEach {
                    loader.postValue(false)
                }.last()
            } catch (e: Exception) {
                loader.postValue(false)
                Result.failure(e)
            }
        }

    override fun logout(): Result<Unit> {
        loader.postValue(true)
        return try {
            googleSignInClient.signOut()
            googleAccount.postValue(null)
            loader.postValue(false)
            Result.success(Unit)
        } catch (e: Exception) {
            loader.postValue(false)
            Result.failure(e)
        }
    }
}