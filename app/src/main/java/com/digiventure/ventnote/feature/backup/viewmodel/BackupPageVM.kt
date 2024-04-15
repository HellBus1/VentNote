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
import com.digiventure.ventnote.data.google_drive.GoogleDriveRepository
import com.google.api.services.drive.model.File
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Calendar
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
        val currentState = _uiState.value.copy(fileSyncState = FileSyncState.SyncStarted)
        _uiState.value = currentState

        try {
            repository.uploadDatabaseFile(
                app.getDatabasePath(Constants.DATABASE_NAME),
                getDatabaseNameWithTimestamps()
            ).onEach {
                _uiState.value = currentState.copy(fileSyncState = FileSyncState.SyncFinished)
                getBackupFileList()
            }.last()
        } catch (e: Exception) {
            val errorMessage = e.message ?: Constants.EMPTY_STRING
            _uiState.value = currentState.copy(fileSyncState = FileSyncState.SyncFailed(errorMessage))
        }
    }

    fun restoreDatabase(fileId: String) = viewModelScope.launch {
        val currentState = _uiState.value.copy(fileSyncState = FileSyncState.SyncStarted)
        _uiState.value = currentState

        Log.d("hasil", fileId)
        try {
            repository.restoreDatabaseFile(app.getDatabasePath(Constants.DATABASE_NAME), fileId)
                .onEach {
                    _uiState.value = currentState.copy(fileSyncState = FileSyncState.SyncFinished)
                }.last()
        } catch (e: Exception) {
            val errorMessage = e.message ?: Constants.EMPTY_STRING
            _uiState.value = currentState.copy(fileSyncState = FileSyncState.SyncFailed(errorMessage))
        }
    }

    fun getBackupFileList() = viewModelScope.launch {
        val currentState = _uiState.value.copy(fileBackupListState = FileBackupListState.FileBackupListStarted)
        _uiState.value = currentState

        try {
            repository.getBackupFileList().collect { result ->
                _uiState.value = currentState.copy(fileBackupListState = FileBackupListState.FileBackupListFinished)
                if (result.isSuccess) {
                    val files = result.getOrNull()
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

    sealed class FileSyncState {
        object SyncStarted : FileSyncState()
        object SyncFinished : FileSyncState()
        data class SyncFailed(val errorMessage: String) : FileSyncState()
    }

    sealed class FileBackupListState {
        object FileBackupListStarted : FileBackupListState()
        object FileBackupListFinished : FileBackupListState()
        data class FileBackupListFailed(val errorMessage: String) : FileBackupListState()
    }

    data class BackupPageState(
        var fileBackupListState: FileBackupListState = FileBackupListState.FileBackupListFinished,
        var fileSyncState: FileSyncState = FileSyncState.SyncFinished
    )

    private fun getDatabaseNameWithTimestamps(): String {
        return Constants.DATABASE_NAME + "_" + Calendar.getInstance().timeInMillis
    }
}