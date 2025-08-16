package com.digiventure.ventnote.feature.backup.viewmodel

import androidx.compose.runtime.State
import androidx.lifecycle.LiveData
import com.google.api.services.drive.model.File

interface BackupPageBaseVM {
    val uiState: State<BackupPageVM.BackupPageState>

    val driveBackupFileList: LiveData<List<File>>

    fun backupDatabase()

    fun restoreDatabase(fileId: String)

    fun getBackupFileList()

    fun deleteDatabase(fileId: String)

    fun clearBackupFileList()
}