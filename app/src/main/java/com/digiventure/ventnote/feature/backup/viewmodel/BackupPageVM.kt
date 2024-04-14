package com.digiventure.ventnote.feature.backup.viewmodel

import android.app.Application
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
        _uiState.value = BackupPageState(fileSyncState = FileSyncState.SyncStarted)
        try {
            repository.uploadDatabaseFile(
                app.getDatabasePath(Constants.DATABASE_NAME),
                getDatabaseNameWithTimestamps()
            ).onEach {
                _uiState.value = BackupPageState(fileSyncState = FileSyncState.SyncFinished)
            }.last()
        } catch (e: Exception) {
            val errorMessage = e.message ?: Constants.EMPTY_STRING
            _uiState.value = BackupPageState(fileSyncState = FileSyncState.SyncFailed(errorMessage))
        }
    }

    fun backupFileList() = viewModelScope.launch {
        try {
            repository.getBackupFileList().collect { result ->
                _uiState.value = BackupPageState(fileBackupListState = FileBackupListState.FileBackupListFinished)
                if (result.isSuccess) {
                    val files = result.getOrNull()
                    _driveBackupFileList.value = files ?: emptyList()
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: Constants.EMPTY_STRING
                    _uiState.value = BackupPageState(
                        fileBackupListState = FileBackupListState.FileBackupListFailed(
                            errorMessage
                        ))
                }
            }
        } catch (e: Exception) {
            val errorMessage = e.message ?: Constants.EMPTY_STRING
            _uiState.value = BackupPageState(
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
        var fileBackupListState: FileBackupListState = FileBackupListState.FileBackupListStarted,
        var fileSyncState: FileSyncState = FileSyncState.SyncFinished
    )

    private fun getDatabaseNameWithTimestamps(): String {
        return Constants.DATABASE_NAME + "_" + Calendar.getInstance().timeInMillis
    }
}