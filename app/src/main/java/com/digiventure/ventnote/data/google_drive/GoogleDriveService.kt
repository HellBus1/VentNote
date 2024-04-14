package com.digiventure.ventnote.data.google_drive

import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import java.io.File as JavaFile

class GoogleDriveService @Inject constructor(
    private val drive: Drive?
) {
    companion object {
        private const val FILE_MIME_TYPE = "application/x-sqlite3"
        private const val APP_DATA_FOLDER_SPACE = "appDataFolder"
    }

    suspend fun uploadDatabaseFile(databaseFile: JavaFile, fileName: String) = withContext(Dispatchers.IO) {
        val metaData = getMetaData(fileName)
        metaData.parents = listOf(APP_DATA_FOLDER_SPACE)
        val bytes = databaseFile.inputStream().readBytes()
        val fileContent = ByteArrayContent(FILE_MIME_TYPE, bytes)
        drive?.files()?.create(metaData, fileContent)?.execute()
        null
    }

    suspend fun readFile(file: JavaFile, fileId: String) = withContext(Dispatchers.IO) {
        drive?.files()?.get(fileId)?.executeAsInputStream()?.use {
            it.copyTo(file.outputStream())
        }
        null
    }

    suspend fun queryFiles(): FileList? = withContext(Dispatchers.IO) {
        drive?.files()?.list()?.setSpaces(APP_DATA_FOLDER_SPACE)?.execute()
    }

    private fun getMetaData(fileName: String): File {
        return File().setMimeType(FILE_MIME_TYPE).setName(fileName)
    }
}