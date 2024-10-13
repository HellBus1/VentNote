package com.digiventure.ventnote.data.google_drive

import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GoogleDriveService @Inject constructor(
    private val scope: CoroutineScope,
    private val proxy: DatabaseProxy,
    private val dispatcher: ExecutorCoroutineDispatcher,
) {
    companion object {
        private const val FILE_MIME_TYPE = "application/json"
        private const val APP_DATA_FOLDER_SPACE = "appDataFolder"
    }

    /**
     * Uploads a database file to Google Drive.
     * @param databaseFile The JavaFile representing the database file.
     * @param fileName The name of the file to be uploaded.
     */
    suspend fun uploadDatabaseFile(notes: List<NoteModel>, fileName: String, drive: Drive?) =
        withContext(dispatcher + scope.coroutineContext) {
            val metaData = getMetaData(fileName)
            metaData.parents = listOf(APP_DATA_FOLDER_SPACE)
            val jsonString = Gson().toJson(notes)
            val fileContent = ByteArrayContent(FILE_MIME_TYPE, jsonString.toByteArray())
            drive?.files()?.create(metaData, fileContent)?.execute()
    }

    /**
     * Reads a file from Google Drive and writes it to the specified JavaFile.
     * @param file The JavaFile to which the file content will be written.
     * @param fileId The ID of the file to be read from Google Drive.
     */
    suspend fun readFile(fileId: String, drive: Drive?) =
        withContext(dispatcher + scope.coroutineContext) {
            val jsonString = drive?.files()?.get(fileId)?.executeMediaAsInputStream()?.use {
                it.bufferedReader().use { reader ->
                    reader.readText()
                }
            }
            val notes = Gson().fromJson(jsonString, Array<NoteModel>::class.java).toList()
            proxy.dao().upsertNotesWithTimestamp(notes)
    }

    /**
     * Queries files from Google Drive within the appDataFolder.
     * @return Returns a FileList object containing the list of files, or null if an error occurs.
     */
    suspend fun queryFiles(drive: Drive?): FileList? = withContext(Dispatchers.IO) {
        drive?.files()?.list()?.setSpaces(APP_DATA_FOLDER_SPACE)?.execute()
    }

    suspend fun deleteFile(fileId: String, drive: Drive?) = withContext(Dispatchers.IO) {
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