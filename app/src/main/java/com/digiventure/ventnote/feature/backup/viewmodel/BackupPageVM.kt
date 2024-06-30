package com.digiventure.ventnote.feature.backup.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.config.DriveAPI
import com.digiventure.ventnote.data.google_drive.GoogleDriveRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BackupPageVM @Inject constructor(
    private val app: Application,
    private val repository: GoogleDriveRepository
): ViewModel() {
    private val _uiState = mutableStateOf(BackupPageState())
    val uiState: State<BackupPageState> = _uiState

    private val _driveBackupFileList = MutableLiveData<List<File>>()
    val driveBackupFileList: LiveData<List<File>> = _driveBackupFileList

    fun backupDatabase() = viewModelScope.launch {
        val currentState = _uiState.value.copy(fileBackupState = FileBackupState.SyncStarted)
        _uiState.value = currentState

        val drive = getDriveInstance()

        try {
            repository.uploadDatabaseFile(
                app.getDatabasePath(Constants.DATABASE_NAME),
                getDatabaseNameWithTimestamps(),
                drive
            ).onEach {
                _uiState.value = currentState.copy(fileBackupState = FileBackupState.SyncFinished)
                getBackupFileList()
            }.last()
        } catch (e: Exception) {
            val errorMessage = e.message ?: Constants.EMPTY_STRING
            _uiState.value = currentState.copy(fileBackupState = FileBackupState.SyncFailed(errorMessage))
        }
    }

    fun restoreDatabase(fileId: String) = viewModelScope.launch {
        val currentState = _uiState.value.copy(fileRestoreState = FileRestoreState.SyncStarted)
        _uiState.value = currentState

        val drive = getDriveInstance()

        try {
            repository.restoreDatabaseFile(app.getDatabasePath(Constants.DATABASE_NAME), fileId, drive)
                .onEach {
                    _uiState.value = currentState.copy(fileRestoreState = FileRestoreState.SyncFinished)
                }.last()
        } catch (e: Exception) {
            val errorMessage = e.message ?: Constants.EMPTY_STRING
            _uiState.value = currentState.copy(fileRestoreState = FileRestoreState.SyncFailed(errorMessage))
        }
    }

    fun getBackupFileList() = viewModelScope.launch {
        val currentState = _uiState.value.copy(fileBackupListState = FileBackupListState.FileBackupListStarted)
        _uiState.value = currentState

        val drive = getDriveInstance()

        try {
            repository.getBackupFileList(drive).collect { result ->
                _uiState.value = currentState.copy(fileBackupListState = FileBackupListState.FileBackupListFinished)
                if (result.isSuccess) {
                    val files = result.getOrNull()
                    Log.d("Called", "getBackupFileList: $files")
                    _driveBackupFileList.value = files ?: emptyList()
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: Constants.EMPTY_STRING
                    _uiState.value = currentState.copy(
                        fileBackupListState = FileBackupListState.FileBackupListFailed(
                            errorMessage
                        ))
                }
            }
        } catch (e: Exception) {
            val errorMessage = e.message ?: Constants.EMPTY_STRING
            _uiState.value = currentState.copy(
                fileBackupListState = FileBackupListState.FileBackupListFailed(
                    errorMessage
                ))
        }
    }

    fun deleteDatabase(fileId: String) = viewModelScope.launch {
        val currentState = _uiState.value.copy(fileDeleteState = FileDeleteState.SyncStarted)
        _uiState.value = currentState

        val drive = getDriveInstance()

        try {
            repository.deleteFile(fileId, drive)
                .onEach {
                    _uiState.value = currentState.copy(fileDeleteState = FileDeleteState.SyncFinished)
                }.last()
        } catch (e: Exception) {
            val errorMessage = e.message ?: Constants.EMPTY_STRING
            _uiState.value = currentState.copy(fileDeleteState = FileDeleteState.SyncFailed(errorMessage))
        }
    }

    private fun getDriveInstance(): Drive? {
        return GoogleSignIn.getLastSignedInAccount(app.applicationContext)?.run {
            DriveAPI.getInstance(app.applicationContext, this)
        }
    }

    sealed class FileBackupState {
        object SyncInitial : FileBackupState()
        object SyncStarted : FileBackupState()
        object SyncFinished : FileBackupState()
        data class SyncFailed(val errorMessage: String) : FileBackupState()
    }

    sealed class FileRestoreState {
        object SyncInitial : FileRestoreState()
        object SyncStarted : FileRestoreState()
        object SyncFinished : FileRestoreState()
        data class SyncFailed(val errorMessage: String) : FileRestoreState()
    }

    sealed class FileDeleteState {
        object SyncInitial : FileDeleteState()
        object SyncStarted : FileDeleteState()
        object SyncFinished : FileDeleteState()
        data class SyncFailed(val errorMessage: String) : FileDeleteState()
    }

    sealed class FileBackupListState {
        object FileBackupListStarted : FileBackupListState()
        object FileBackupListFinished : FileBackupListState()
        data class FileBackupListFailed(val errorMessage: String) : FileBackupListState()
    }

    data class BackupPageState(
        var fileBackupListState: FileBackupListState = FileBackupListState.FileBackupListFinished,
        var fileBackupState: FileBackupState = FileBackupState.SyncInitial,
        var fileRestoreState: FileRestoreState = FileRestoreState.SyncInitial,
        var fileDeleteState: FileDeleteState = FileDeleteState.SyncInitial
    )

    private fun getDatabaseNameWithTimestamps(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.getDefault())
        val timestamp = dateFormat.format(Calendar.getInstance().time)
        return Constants.DATABASE_NAME + "_" + timestamp
    }
}