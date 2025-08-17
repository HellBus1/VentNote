package com.digiventure.ventnote.feature.backup.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.config.DriveAPI
import com.digiventure.ventnote.data.google_drive.GoogleDriveRepository
import com.digiventure.ventnote.data.persistence.NoteRepository
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
    private val repository: GoogleDriveRepository,
    private val databaseRepository: NoteRepository
): ViewModel(), BackupPageBaseVM {
    private val _uiState = mutableStateOf(BackupPageState())
    override val uiState: State<BackupPageState> = _uiState

    private val _driveBackupFileList = MutableLiveData<List<File>>()
    override val driveBackupFileList: LiveData<List<File>> = _driveBackupFileList

    override fun backupDatabase() {
        viewModelScope.launch {
            val currentState = _uiState.value.copy(fileBackupState = FileBackupState.SyncStarted)
            _uiState.value = currentState

            try {
                val drive = getDriveInstance()
                databaseRepository.getNoteList(Constants.UPDATED_AT, Constants.DESCENDING)
                    .collect {
                        repository.uploadDatabaseFile(
                            it.getOrDefault(listOf()),
                            getDatabaseNameWithTimestamps(),
                            drive
                        ).onEach {
                            _uiState.value = currentState.copy(fileBackupState = FileBackupState.SyncFinished)
                            getBackupFileList()
                        }.last()
                    }
            } catch (e: Exception) {
                val errorMessage = e.message ?: Constants.EMPTY_STRING
                _uiState.value = currentState.copy(fileBackupState = FileBackupState.SyncFailed(errorMessage))
            }
        }
    }

    override fun restoreDatabase(fileId: String) {
        viewModelScope.launch {
            val currentState = _uiState.value.copy(fileRestoreState = FileRestoreState.SyncStarted)
            _uiState.value = currentState

            try {
                val drive = getDriveInstance()
                repository.restoreDatabaseFile(fileId, drive)
                    .onEach {
                        _uiState.value = currentState.copy(fileRestoreState = FileRestoreState.SyncFinished)
                    }.last()
            } catch (e: Exception) {
                val errorMessage = e.message ?: Constants.EMPTY_STRING
                _uiState.value = currentState.copy(fileRestoreState = FileRestoreState.SyncFailed(errorMessage))
            }
        }
    }

    override fun getBackupFileList() {
        viewModelScope.launch {
            val currentState = _uiState.value.copy(listOfBackupFileState = FileBackupListState.FileBackupListStarted)
            _uiState.value = currentState

            try {
                val drive = getDriveInstance()
                repository.getBackupFileList(drive).collect { result ->
                    _uiState.value = currentState.copy(listOfBackupFileState = FileBackupListState.FileBackupListFinished)
                    if (result.isSuccess) {
                        val files = result.getOrNull()
                        _driveBackupFileList.value = files ?: emptyList()
                    } else {
                        val errorMessage = result.exceptionOrNull()?.message ?: Constants.EMPTY_STRING
                        _uiState.value = currentState.copy(
                            listOfBackupFileState = FileBackupListState.FileBackupListFailed(
                                errorMessage
                            ))
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: Constants.EMPTY_STRING
                _uiState.value = currentState.copy(
                    listOfBackupFileState = FileBackupListState.FileBackupListFailed(
                        errorMessage
                    ))
            }
        }
    }

    override fun deleteDatabase(fileId: String) {
        viewModelScope.launch {
            val currentState = _uiState.value.copy(fileDeleteState = FileDeleteState.SyncStarted)
            _uiState.value = currentState

            try {
                val drive = getDriveInstance()
                repository.deleteFile(fileId, drive)
                    .onEach {
                        _uiState.value = currentState.copy(fileDeleteState = FileDeleteState.SyncFinished)
                    }.last()
            } catch (e: Exception) {
                val errorMessage = e.message ?: Constants.EMPTY_STRING
                _uiState.value = currentState.copy(fileDeleteState = FileDeleteState.SyncFailed(errorMessage))
            }
        }
    }

    override fun clearBackupFileList() {
        _driveBackupFileList.value = emptyList()
        _uiState.value = _uiState.value.copy(
            listOfBackupFileState = FileBackupListState.FileBackupListFinished,
            fileBackupState = FileBackupState.SyncInitial,
            fileRestoreState = FileRestoreState.SyncInitial,
            fileDeleteState = FileDeleteState.SyncInitial
        )
    }

    private fun getDriveInstance(): Drive? {
        return GoogleSignIn.getLastSignedInAccount(app.applicationContext)?.run {
            DriveAPI.getInstance(app.applicationContext, this)
        }
    }

    sealed class FileBackupState {
        data object SyncInitial : FileBackupState()
        data object SyncStarted : FileBackupState()
        data object SyncFinished : FileBackupState()
        data class SyncFailed(val errorMessage: String) : FileBackupState()
    }

    sealed class FileRestoreState {
        data object SyncInitial : FileRestoreState()
        data object SyncStarted : FileRestoreState()
        data object SyncFinished : FileRestoreState()
        data class SyncFailed(val errorMessage: String) : FileRestoreState()
    }

    sealed class FileDeleteState {
        data object SyncInitial : FileDeleteState()
        data object SyncStarted : FileDeleteState()
        data object SyncFinished : FileDeleteState()
        data class SyncFailed(val errorMessage: String) : FileDeleteState()
    }

    sealed class FileBackupListState {
        data object FileBackupListStarted : FileBackupListState()
        data object FileBackupListFinished : FileBackupListState()
        data class FileBackupListFailed(val errorMessage: String) : FileBackupListState()
    }

    data class BackupPageState(
        var listOfBackupFileState: FileBackupListState = FileBackupListState.FileBackupListFinished,
        var fileBackupState: FileBackupState = FileBackupState.SyncInitial,
        var fileRestoreState: FileRestoreState = FileRestoreState.SyncInitial,
        var fileDeleteState: FileDeleteState = FileDeleteState.SyncInitial
    )

    private fun getDatabaseNameWithTimestamps(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timestamp = dateFormat.format(Calendar.getInstance().time)
        val jsonFormat = ".json"
        return Constants.BACKUP_FILE_NAME + "_" + timestamp + jsonFormat
    }
}