package com.digiventure.ventnote.data.google_drive

import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GoogleDriveService @Inject constructor(
    private val proxy: DatabaseProxy,
) {
    companion object {
        private const val FILE_MIME_TYPE = "application/json"
        private const val APP_DATA_FOLDER_SPACE = "appDataFolder"
    }

    /**
     * Uploads a database file to Google Drive as a JSON file.
     *
     * @param notes The list of all database content.
     * @param fileName The name of the file to be uploaded.
     * @param drive The Google Drive instance.
     * @return A `Result` object indicating success or failure.
     *         On success, the `Result` will contain a success value (`Unit`).
     *         On failure, the `Result` will contain an `Exception` describing the error.
     * @throws Exception If an error occurs during the upload.
     */
    suspend fun uploadDatabaseFile(notes: List<NoteModel>, fileName: String, drive: Drive?): Result<File?> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val metaData = getMetaData(fileName)
                metaData.parents = listOf(APP_DATA_FOLDER_SPACE)
                val jsonString = Gson().toJson(notes)
                val fileContent = ByteArrayContent(FILE_MIME_TYPE, jsonString.toByteArray())
                val result = drive?.files()?.create(metaData, fileContent)?.execute()
                Result.success(result)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Reads a JSON file from Google Drive and writes its contents to the database.
     *
     * @param fileId The ID of the file to be read from Google Drive.
     * @param drive The Google Drive instance.
     * @return A `Result` object indicating success or failure.
     *         On success, the `Result` will contain a success value (`Unit`).
     *         On failure, the `Result` will contain an `Exception` describing the error.
     * @throws Exception If an error occurs during the read operation.
     */
    suspend fun readFile(fileId: String, drive: Drive?): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val jsonString = drive?.files()?.get(fileId)?.executeMediaAsInputStream()?.use {
                it.bufferedReader().use { reader -> reader.readText() }
            }

            val notes = jsonString?.let {
                Gson().fromJson(it, Array<NoteModel>::class.java).toList()
            } ?: emptyList()

            proxy.dao().upsertNotesWithTimestamp(notes)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Queries files from Google Drive within the appDataFolder.
     *
     * @param drive The Google Drive instance.
     * @return A `Result` object containing a list of `DriveFile` objects on success,
     *         or an `Exception` describing the error on failure.
     * @throws Exception If an error occurs during the query.
     */
    suspend fun queryFiles(drive: Drive?): Result<FileList?> = withContext(Dispatchers.IO) {
        return@withContext try {
            val fileList = drive?.files()?.list()?.setSpaces(APP_DATA_FOLDER_SPACE)?.execute()
            Result.success(fileList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deletes a file from Google Drive.
     *
     * @param fileId The ID of the file to be deleted.
     * @param drive The Google Drive instance.
     * @return A `Result` object indicating success or failure.
     *         On success, the `Result` will contain a success value (`Unit`).
     *         On failure, the `Result` will contain an `Exception` describing the error.
     * @throws Exception If an error occurs during the deletion.
     */
    suspend fun deleteFile(fileId: String, drive: Drive?): Result<Void?> = withContext(Dispatchers.IO) {
        return@withContext try {
            val test = drive?.files()?.delete(fileId)?.execute()
            Result.success(test)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Creates and returns metadata for the given file name.
     * @param fileName The name of the file.
     * @return a File object with metadata.
     */
    private fun getMetaData(fileName: String): File {
        return File().setMimeType(FILE_MIME_TYPE).setName(fileName)
    }
}