package com.digiventure.ventnote.data.google_api

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import com.google.api.services.drive.model.File as DriveFile


class GoogleAPIService @Inject constructor(
    private val basePath: DatabaseFiles
) {
    suspend fun uploadDBtoDrive(credential: GoogleAccountCredential) = withContext(Dispatchers.IO) {
        val googleApiHelper = GoogleApiHelper(credential)
        val drive = googleApiHelper.driveInstance ?: throw Exception("Drive instance is null")

        val backupFolderName = "VentNoteDataBackup"
        val folderQuery = drive.files().list()
            .setQ("mimeType='application/vnd.google-apps.folder' and trashed=false and name='$backupFolderName'")
        val folderList = folderQuery.execute().files

        val backupFolderId = if (folderList.isNotEmpty()) {
            folderList[0].id
        } else {
            val backupFolder = DriveFile().apply {
                name = backupFolderName
                mimeType = "application/vnd.google-apps.folder"
            }
            drive.files().create(backupFolder).setFields("id").execute().id
        }

        val databaseFiles = listOf(
            Pair(File(basePath.database), "note_database"),
            Pair(File(basePath.databaseShm), "note_database-shm"),
            Pair(File(basePath.databaseWal), "note_database-wal")
        )

        if (!databaseFiles.all { it.first.exists() }) {
            throw Exception("Room database file does not exist")
        }

        for ((file, name) in databaseFiles) {
            val fileQuery = drive.files().list()
                .setQ("parents='$backupFolderId' and trashed=false and name='$name'")
            val fileList = fileQuery.execute().files

            val storageFile = if (fileList.isNotEmpty()) {
                fileList[0]
            } else {
                DriveFile().apply {
                    parents = listOf(backupFolderId)
                    this.name = name
                }
            }

            val mediaContent = FileContent("application/x-sqlite3", file)

            val updatedFile = if (storageFile.id != null) {
                drive.files().update(storageFile.id, storageFile, mediaContent).execute()
            } else {
                drive.files().create(storageFile, mediaContent).execute()
            }

            if (updatedFile == null) {
                throw Exception("Failed to upload file $name")
            }
        }
    }
}