package com.digiventure.ventnote.feature.backup.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.config.DriveAPI
import com.digiventure.ventnote.config.NoteDatabase
import com.digiventure.ventnote.data.google_drive.GoogleDriveService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class BackupPageVM @Inject constructor(
    private val app: Application,
    private val noteDatabase: NoteDatabase
): ViewModel() {
    private val _eventFlow = MutableSharedFlow<FileSyncEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var googleDriveService: GoogleDriveService? = null
    private val mutex = Mutex()

    val loader = MutableLiveData<Boolean>()

    init {
        initializeDriveAPI()
    }

    private fun initializeDriveAPI() {
        val lastUser: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(app.applicationContext)
        if (lastUser != null) {
            val driveAPI = DriveAPI.getInstance(app.applicationContext, lastUser)
            googleDriveService = GoogleDriveService(driveAPI)
        }
    }

//    val backupFileList: LiveData<Result<FileList?>> = liveData {
//        loader.postValue(true)
//        try {
//            val result = withContext(Dispatchers.IO) {
//                googleDriveService?.queryFiles()
//            }
//            Log.d("hasil", result.toString())
//            loader.postValue(false)
//            emit(Result.success(result))
//        } catch (e: Exception) {
//            loader.postValue(false)
//            emit(Result.failure(e))
//        }
//    }

    suspend fun backupDatabase() = viewModelScope.launch {
        googleDriveService?.uploadDatabaseFile(
            app.getDatabasePath(Constants.DATABASE_NAME), getDatabaseNameWithDate()
        )
    }

    suspend fun restore(fileId: String) = viewModelScope.launch {
        val fileToPopulate = app.getDatabasePath(Constants.DATABASE_NAME)
        googleDriveService?.readFile(
            fileToPopulate, fileId
        )
    }

    suspend fun listOfBackupFiles() = viewModelScope.launch {
        try {
            val files = googleDriveService?.queryFiles()
            Log.d("hasil", files?.files.toString())
        } catch (error: Error) {
            Log.e("hasil", error.toString())
        }
    }

    sealed class FileSyncEvents {
        object SyncStarted : FileSyncEvents()
        object SyncFinished : FileSyncEvents()
        object SyncFailed : FileSyncEvents()
    }

    private fun getDatabaseNameWithDate(): String {
        return Constants.DATABASE_NAME + "_" + Calendar.getInstance().time
    }
}