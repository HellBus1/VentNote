package com.digiventure.ventnote.data.google_drive

import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
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
     * @throws IOException If an I/O error occurs during the upload.
     * @throws GoogleJsonResponseException If the Google Drive API returns an error.
     */
    suspend fun uploadDatabaseFile(notes: List<NoteModel>, fileName: String, drive: Drive?): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val metaData = getMetaData(fileName)
            metaData.parents = listOf(APP_DATA_FOLDER_SPACE)
            val jsonString = Gson().toJson(notes)
            val fileContent = ByteArrayContent(FILE_MIME_TYPE, jsonString.toByteArray())
            drive?.files()?.create(metaData, fileContent)?.execute()
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: GoogleJsonResponseException) {
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
     * @throws IOException If an I/O error occurs during the read operation.
     * @throws GoogleJsonResponseException If the Google Drive API returns an error.
     * @throws JsonSyntaxException If the JSON file is malformed or cannot be parsed.
     * @throws IllegalArgumentException If the provided `fileId` is null or blank.
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
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: GoogleJsonResponseException) {
            Result.failure(e)
        } catch (e: JsonSyntaxException) {
            Result.failure(e)
        }
    }

    /**
     * Queries files from Google Drive within the appDataFolder.
     *
     * @param drive The Google Drive instance.
     * @return A `Result` object containing a list of `DriveFile` objects on success,
     *         or an `Exception` describing the error on failure.
     * @throws IOException If an I/O error occurs during the query.
     * @throws GoogleJsonResponseException If the Google Drive API returns an error.
     */
    suspend fun queryFiles(drive: Drive?): Result<FileList?> = withContext(Dispatchers.IO) {
        return@withContext try {
            val fileList = drive?.files()?.list()?.setSpaces(APP_DATA_FOLDER_SPACE)?.execute()
            Result.success(fileList)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: GoogleJsonResponseException) {
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
     * @throws IOException If an I/O error occurs during the deletion.
     * @throws GoogleJsonResponseException If the Google Drive API returns an error.
     */
    suspend fun deleteFile(fileId: String, drive: Drive?): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            drive?.files()?.delete(fileId)?.execute()
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: GoogleJsonResponseException) {
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