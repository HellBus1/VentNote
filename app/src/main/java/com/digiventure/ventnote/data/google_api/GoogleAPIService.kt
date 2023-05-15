package com.digiventure.ventnote.data.google_api

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import com.google.api.services.drive.model.File as DriveFile


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
            val drive = GoogleApiHelper(credential).driveInstance ?: throw Exception("Drive instance is null")

            val backupFolderId = getOrCreateBackupFolderId(drive)

            val files = drive.files().list()
                .setQ("trashed=false and '$backupFolderId' in parents")
                .setFields("nextPageToken, files(id, name, createdTime)")
                .setPageSize(10)
                .execute()

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
                        drive.files().get(file.id).executeMediaAndDownloadTo(FileOutputStream(basePath.database))
                    } else if (file.name.equals(databaseShm)) {
                        drive.files().get(file.id).executeMediaAndDownloadTo(FileOutputStream(basePath.databaseShm))
                    } else if (file.name.equals(databaseWal)) {
                        drive.files().get(file.id).executeMediaAndDownloadTo(FileOutputStream(basePath.databaseWal))
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun uploadDBtoDrive(credential: GoogleAccountCredential) = withContext(Dispatchers.IO) {
        val drive = GoogleApiHelper(credential).driveInstance ?: throw Exception("Drive instance is null")

        val backupFolderId = getOrCreateBackupFolderId(drive)

        if (!databaseFiles.all { it.first.exists() }) {
            throw Exception("Room database file does not exist")
        }

        for ((file, name) in databaseFiles) {
            val existingFile = getExistingFileInDrive(drive, backupFolderId, name)
            if (existingFile != null) {
                updateDatabaseFileInDrive(drive, existingFile, file)
            } else {
                uploadDatabaseFileToDrive(drive, backupFolderId, file, name)
            }
        }
    }

    private fun getOrCreateBackupFolderId(drive: Drive): String {
        val folderQuery = drive.files().list()
            .setQ("mimeType='application/vnd.google-apps.folder' and trashed=false and name='$parentFolderName'")
        val folderList = folderQuery.execute().files

        return if (folderList.isNotEmpty()) {
            folderList[0].id
        } else {
            val backupFolder = DriveFile().apply {
                name = parentFolderName
                mimeType = "application/vnd.google-apps.folder"
            }
            drive.files().create(backupFolder).setFields("id").execute().id
        }
    }

    private fun getExistingFileInDrive(drive: Drive, backupFolderId: String, fileName: String): DriveFile? {
        val query = drive.files().list()
            .setQ("name='$fileName' and trashed=false and '$backupFolderId' in parents")
            .setFields("files(id)")
            .execute()

        return query.files.firstOrNull()
    }

    private fun updateDatabaseFileInDrive(drive: Drive, existingFile: DriveFile, file: File) {
        val mediaContent = FileContent("application/x-sqlite3", file)
        drive.files().update(existingFile.id, null, mediaContent).execute()
    }


    private fun uploadDatabaseFileToDrive(
        drive: Drive,
        backupFolderId: String,
        file: File,
        name: String
    ): DriveFile {
        val storageFile = DriveFile().apply {
            parents = listOf(backupFolderId)
            this.name = name
        }
        val mediaContent = FileContent("application/x-sqlite3", file)
        return drive.files().create(storageFile, mediaContent).execute()
    }
}