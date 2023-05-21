package com.digiventure.ventnote.feature.note_backup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.digiventure.ventnote.data.NoteRepository
import com.digiventure.ventnote.data.data_store.DataStoreHelper
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.abs

const val savedDayKey = "savedDay"
const val maxSyncAttemptKey = "maxSyncAttemptKey"

@HiltViewModel
class NoteBackupPageVM @Inject constructor(
    private val repository: NoteRepository,
    private val googleSignInClient: GoogleSignInClient,
    private val dataStoreHelper: DataStoreHelper
): ViewModel(), NoteBackupPageBaseVM {
    override val loader = MutableLiveData<Boolean>()

    override val signInClient: GoogleSignInClient
        get() = googleSignInClient

    override val googleAccount: MutableLiveData<GoogleSignInAccount?> = MutableLiveData()
    override val savedDay: LiveData<Long>
        get() = liveData { emitSource(dataStoreHelper.getLongData(savedDayKey).asLiveData()) }
    override val maxAttempts: LiveData<Int>
        get() = liveData { emitSource(dataStoreHelper.getIntData(maxSyncAttemptKey).asLiveData()) }

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

    override suspend fun setSavedDay(previousDay: Long, currentTime: Long): Result<Unit> {
        val elapsedTime = abs(System.currentTimeMillis() - previousDay)
        val oneHourInMillis = 60 * 60 * 1000

        return if (previousDay == 0L || (elapsedTime >= oneHourInMillis)) {
            try {
                dataStoreHelper.setLongData(savedDayKey, currentTime)
                dataStoreHelper.setIntData(maxSyncAttemptKey, 4)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    override suspend fun setMaxSyncAttempt(value: Int): Result<Unit> {
        return try {
            dataStoreHelper.setIntData(maxSyncAttemptKey, value)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}