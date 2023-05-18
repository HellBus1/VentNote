package com.digiventure.ventnote.data.google_api

import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import java.io.FileOutputStream


class GoogleApiHelper constructor(
    private val credential: GoogleAccountCredential
) {
    private var driveInstance: Drive? = null

    companion object {
        private var instance: GoogleApiHelper? = null

        fun getInstance(credential: GoogleAccountCredential): GoogleApiHelper {
            return instance ?: synchronized(this) {
                instance ?: GoogleApiHelper(credential).also { instance = it }
            }
        }
    }

    private fun getDriveInstance(): Drive {
        return driveInstance ?: synchronized(this) {
            driveInstance ?: createDriveInstance().also { driveInstance = it }
        }
    }

    private fun createDriveInstance(): Drive {
        return Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential
        )
            .setApplicationName("VentNote")
            .build()
    }

    fun getListOfFileInsideAFolder(folderId: String): FileList {
        val drive = getDriveInstance()

        return drive.files().list()
            .setQ("trashed=false and '$folderId' in parents")
            .setFields("nextPageToken, files(id, name, createdTime)")
            .setPageSize(10)
            .execute()
    }

    fun downloadFile(googleFileId: String, pathToSave: String) {
        val drive = getDriveInstance()

        return drive.files().get(googleFileId).executeMediaAndDownloadTo(FileOutputStream(pathToSave))
    }

    fun getOrCreateBackupFolderId(parentFolderName: String): String {
        val drive = getDriveInstance()
        val folderQuery = drive.files().list()
            .setQ("mimeType='application/vnd.google-apps.folder' and trashed=false and name='$parentFolderName'")
        val folderList = folderQuery.execute().files

        return if (folderList.isNotEmpty()) {
            folderList[0].id
        } else {
            val backupFolder = File().apply {
                name = parentFolderName
                mimeType = "application/vnd.google-apps.folder"
            }
            drive.files().create(backupFolder).setFields("id").execute().id
        }
    }

    fun getExistingFileInDrive(backupFolderId: String, fileName: String): File? {
        val drive = getDriveInstance()
        val query = drive.files().list()
            .setQ("name='$fileName' and trashed=false and '$backupFolderId' in parents")
            .setFields("files(id)")
            .execute()

        return query.files.firstOrNull()
    }

    fun updateDatabaseFileInDrive(existingFile: File, file: java.io.File) {
        val drive = getDriveInstance()
        val mediaContent = FileContent("application/x-sqlite3", file)
        val updateRequest = drive.files().update(existingFile.id, null, mediaContent)
        updateRequest.isKeepRevisionForever = true
        updateRequest.execute()
    }

    fun uploadDatabaseFileToDrive(
        backupFolderId: String,
        file: java.io.File,
        name: String
    ): File {
        val drive = getDriveInstance()
        val storageFile = File().apply {
            parents = listOf(backupFolderId)
            this.name = name
        }
        val mediaContent = FileContent("application/x-sqlite3", file)
        return drive.files().create(storageFile, mediaContent).execute()
    }
}