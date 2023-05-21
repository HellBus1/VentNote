package com.digiventure.ventnote.data.google_api

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class GoogleAPIService @Inject constructor(
    private val databaseFiles: List<FileInfo>,
    private val googleAPIHelperLayer: GoogleAPIHelperLayer
) {
    suspend fun uploadDBtoDrive(credential: GoogleAccountCredential) = withContext(Dispatchers.IO) {
        try {
            val googleApiHelper = googleAPIHelperLayer.getGoogleAPIHelperInstance(credential)
            val backupFolderId = googleApiHelper.getOrCreateBackupFolderId("VentNoteDataBackup")

            databaseFiles.forEach { (file, name) ->
                val existingFile = googleApiHelper.getExistingFileInDrive(backupFolderId, name)
                if (existingFile != null && file.exists()) {
                    googleApiHelper.updateDatabaseFileInDrive(existingFile, file)
                } else if (file.exists()) {
                    googleApiHelper.uploadDatabaseFileToDrive(backupFolderId, file, name)
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun syncDBFromDrive(credential: GoogleAccountCredential) = withContext(Dispatchers.IO) {
        try {
            val googleApiHelper = googleAPIHelperLayer.getGoogleAPIHelperInstance(credential)
            val backupFolderId = googleApiHelper.getOrCreateBackupFolderId("VentNoteDataBackup")
            val files = googleApiHelper.getListOfFileInsideAFolder(backupFolderId)

            if (files.files.isEmpty()) {
                throw Exception("No file exists in VentNoteBackup")
            }

            val databaseFilesMap = databaseFiles.associateBy { it.name }

            databaseFiles.forEach { fileInfo ->
                if (fileInfo.file.exists()) {
                    fileInfo.file.delete()
                }
            }

            files.files.forEach { file ->
                val fileInfoObj = databaseFilesMap[file.name]

                if (fileInfoObj != null) {
                    googleApiHelper.downloadFile(file.id, fileInfoObj.file.absolutePath)
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }
}