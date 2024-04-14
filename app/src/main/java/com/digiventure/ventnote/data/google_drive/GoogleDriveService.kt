package com.digiventure.ventnote.data.google_drive

import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.io.File as JavaFile

class GoogleDriveService (
    private val drive: Drive,
    private val dispatcher: CoroutineDispatcher = Executors.newSingleThreadExecutor()
        .asCoroutineDispatcher(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())
) {
    companion object {
        private const val FILE_MIME_TYPE = "application/x-sqlite3"
        private const val APP_DATA_FOLDER_SPACE = "appDataFolder"
    }

    suspend fun uploadDatabaseFile(databaseFile: JavaFile, fileName: String) = withContext(dispatcher + scope.coroutineContext) {
        val metaData = getMetaData(fileName)
        metaData.parents = listOf(APP_DATA_FOLDER_SPACE)
        val bytes = databaseFile.inputStream().readBytes()
        val fileContent = ByteArrayContent(FILE_MIME_TYPE, bytes)
        drive.files().create(metaData, fileContent).execute()
        null
    }

    suspend fun readFile(file: JavaFile, fileId: String) = withContext(scope.coroutineContext) {
        drive.files()[fileId].executeAsInputStream()?.use {
            it.copyTo(file.outputStream())
        }
        null
    }

    suspend fun queryFiles(): FileList = withContext(dispatcher) {
        drive.files().list().setSpaces(APP_DATA_FOLDER_SPACE).execute()
    }

    private fun getMetaData(fileName: String): File {
        return File().setMimeType(FILE_MIME_TYPE).setName(fileName)
    }
}