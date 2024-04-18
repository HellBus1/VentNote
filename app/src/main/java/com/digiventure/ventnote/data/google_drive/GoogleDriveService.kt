package com.digiventure.ventnote.data.google_drive

import com.digiventure.ventnote.module.proxy.DatabaseProxy
import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import java.io.File as JavaFile

class GoogleDriveService @Inject constructor(
    private val drive: Drive?,
    private val proxy: DatabaseProxy,
    private val mutex: Mutex,
    private val scope: CoroutineScope,
    private val dispatcher: ExecutorCoroutineDispatcher,
) {
    companion object {
        private const val FILE_MIME_TYPE = "application/x-sqlite3"
        private const val APP_DATA_FOLDER_SPACE = "appDataFolder"
    }

    /**
     * Uploads a database file to Google Drive.
     * @param databaseFile The JavaFile representing the database file.
     * @param fileName The name of the file to be uploaded.
     */
    suspend fun uploadDatabaseFile(databaseFile: JavaFile, fileName: String) = withContext(dispatcher + scope.coroutineContext) {
        mutex.withLock {
            proxy.reset()

            val metaData = getMetaData(fileName)
            metaData.parents = listOf(APP_DATA_FOLDER_SPACE)
            val bytes = databaseFile.inputStream().readBytes()
            val fileContent = ByteArrayContent(FILE_MIME_TYPE, bytes)
            val file = drive?.files()?.create(metaData, fileContent)?.execute()
            queryFiles()?.files?.forEach {
                if (file?.id != it.id) deleteFile(it.id)
            }
        }
    }

    /**
     * Reads a file from Google Drive and writes it to the specified JavaFile.
     * @param file The JavaFile to which the file content will be written.
     * @param fileId The ID of the file to be read from Google Drive.
     */
    suspend fun readFile(file: JavaFile, fileId: String) = withContext(dispatcher + scope.coroutineContext) {
        mutex.withLock {
            proxy.reset()

            drive?.files()?.get(fileId)?.executeMediaAsInputStream()?.use {
                file.delete()
                it.copyTo(file.outputStream())
            }
        }
    }

    /**
     * Queries files from Google Drive within the appDataFolder.
     * @return Returns a FileList object containing the list of files, or null if an error occurs.
     */
    suspend fun queryFiles(): FileList? = withContext(Dispatchers.IO) {
        drive?.files()?.list()?.setSpaces(APP_DATA_FOLDER_SPACE)?.execute()
    }

    private suspend fun deleteFile(fileId: String) = withContext(Dispatchers.IO) {
        drive?.files()?.delete(fileId)?.execute()
        fileId
    }

    /**
     * Creates and returns metadata for the given file name.
     * @param fileName The name of the file.
     * @return Returns a File object with metadata.
     */
    private fun getMetaData(fileName: String): File {
        return File().setMimeType(FILE_MIME_TYPE).setName(fileName)
    }
}