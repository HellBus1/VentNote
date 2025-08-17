package com.digiventure.ventnote.feature.backup.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.api.services.drive.model.File

class BackupPageMockVM: ViewModel(), BackupPageBaseVM {
    private val _uiState = mutableStateOf(BackupPageVM.BackupPageState())
    override val uiState: State<BackupPageVM.BackupPageState> = _uiState

    private val dummyGoogleDriveFileOne: File = File()
        .setName("dummy_file_2024-20-10.db")
    private val dummyGoogleDriveFileTwo: File = File()
        .setName("dummy_file_29-90-129201.db")

    private val _driveBackupFileList = MutableLiveData(
        listOf(
            dummyGoogleDriveFileOne,
            dummyGoogleDriveFileTwo
        )
//        emptyList<File>()
    )
    override val driveBackupFileList: LiveData<List<File>> = _driveBackupFileList

    init {
        _uiState.value = _uiState.value.copy(
            listOfBackupFileState = BackupPageVM.FileBackupListState.FileBackupListFailed("error")
//            listOfBackupFileState = BackupPageVM.FileBackupListState.FileBackupListFinished
        )
    }

    override fun backupDatabase() {

    }

    override fun restoreDatabase(fileId: String) {

    }

    override fun getBackupFileList() {

    }

    override fun deleteDatabase(fileId: String) {

    }

    override fun clearBackupFileList() {

    }
}