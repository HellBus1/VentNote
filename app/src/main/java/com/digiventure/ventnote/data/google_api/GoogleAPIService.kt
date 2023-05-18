package com.digiventure.ventnote.data.google_api

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


class GoogleAPIService @Inject constructor(
    private val basePath: DatabaseFiles
) {
    private val database = "note_database"
    private val databaseShm = "note_database-shm"
    private val databaseWal = "note_database-wal"

    private val parentFolderName = "VentNoteDataBackup"

    private val databaseFiles = listOf(
        Pair(File(basePath.database), database),
        Pair(File(basePath.databaseShm), databaseShm),
        Pair(File(basePath.databaseWal), databaseWal)
    )

    suspend fun syncDBFromDrive(credential: GoogleAccountCredential) = withContext(Dispatchers.IO) {
        try {
            val googleApiHelper = GoogleApiHelper.getInstance(credential)

            val backupFolderId = googleApiHelper.getOrCreateBackupFolderId(parentFolderName)

            val files = googleApiHelper.getListOfFileInsideAFolder(backupFolderId)

            if (files.files.isEmpty()) {
                throw Exception("No file exists in VentNoteBackup")
            } else {
                if (databaseFiles.all { it.first.exists() }) {
                    for ((file) in databaseFiles) {
                        file.delete()
                    }
                }

                files.files.forEach { file ->
                    if (file.name.equals(database)) {
                        googleApiHelper.downloadFile(file.id, basePath.database)
                    } else if (file.name.equals(databaseShm)) {
                        googleApiHelper.downloadFile(file.id, basePath.databaseShm)
                    } else if (file.name.equals(databaseWal)) {
                        googleApiHelper.downloadFile(file.id, basePath.databaseWal)
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun uploadDBtoDrive(credential: GoogleAccountCredential) = withContext(Dispatchers.IO) {
        try {
            val googleApiHelper = GoogleApiHelper.getInstance(credential)

            val backupFolderId = googleApiHelper.getOrCreateBackupFolderId(parentFolderName)

            if (!databaseFiles.all { it.first.exists() }) {
                throw Exception("Room database file does not exist")
            }

            for ((file, name) in databaseFiles) {
                val existingFile = googleApiHelper.getExistingFileInDrive(backupFolderId, name)
                if (existingFile != null) {
                    googleApiHelper.updateDatabaseFileInDrive(existingFile, file)
                } else {
                    googleApiHelper.uploadDatabaseFileToDrive(backupFolderId, file, name)
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }
}