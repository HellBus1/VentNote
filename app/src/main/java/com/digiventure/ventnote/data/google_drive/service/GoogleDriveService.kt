package com.digiventure.ventnote.data.google_drive

import android.app.Application
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.NoteTagCrossRef
import com.digiventure.ventnote.data.persistence.TagModel
import com.digiventure.ventnote.feature.widget.WidgetRefresher
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GoogleDriveService @Inject constructor(
    private val app: Application,
    private val proxy: DatabaseProxy,
    private val refresher: WidgetRefresher
) {
    companion object {
        private const val FILE_MIME_TYPE = "application/json"
        private const val APP_DATA_FOLDER_SPACE = "appDataFolder"

        fun createBackupGson(): Gson {
            return GsonBuilder()
                .registerTypeAdapter(Date::class.java, DateTypeAdapter())
                .create()
        }
    }

    private val gson: Gson = createBackupGson()

    /**
     * Uploads the full database as a [BackupPayload] JSON to Google Drive.
     *
     * @param payload The complete backup payload (notes + tags + noteTags).
     * @param fileName The name of the file to be uploaded.
     * @param drive The Google Drive instance.
     */
    suspend fun uploadDatabaseFile(payload: BackupPayload, fileName: String, drive: Drive?): Result<File?> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val metaData = getMetaData(fileName)
                metaData.parents = listOf(APP_DATA_FOLDER_SPACE)
                val jsonString = gson.toJson(payload)
                val fileContent = ByteArrayContent(FILE_MIME_TYPE, jsonString.toByteArray())
                val result = drive?.files()?.create(metaData, fileContent)?.execute()
                Result.success(result)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Reads a JSON file from Google Drive and restores its contents to the database.
     *
     * Supports two formats:
     * 1. **New format** (version 1+): `{"version":1,"notes":[...],"tags":[...],"noteTags":[...]}`
     * 2. **Legacy format** (version 0): a plain JSON array `[{note},{note},...]`
     *
     * Inspects the JSON structure (object vs array) rather than speculative deserialization
     * to prevent misleading errors.
     */
    suspend fun readFile(fileId: String, drive: Drive?): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val jsonString = drive?.files()?.get(fileId)?.executeMediaAsInputStream()?.use {
                it.bufferedReader().use { reader -> reader.readText() }
            } ?: return@withContext Result.failure(Exception("Empty file"))

            val jsonElement = try {
                JsonParser.parseString(jsonString)
            } catch (e: Exception) {
                return@withContext Result.failure(Exception("Invalid JSON backup: ${e.message}", e))
            }

            if (jsonElement.isJsonObject) {
                // New format — restore notes, tags, and note-tag cross refs
                val payload = gson.fromJson(jsonElement.asJsonObject, BackupPayload::class.java)
                val notes = payload?.notes ?: emptyList()
                val tags = payload?.tags ?: emptyList()
                val noteTags = payload?.noteTags ?: emptyList()

                proxy.dao().upsertNotes(notes)
                if (tags.isNotEmpty()) {
                    upsertTags(tags)
                }
                if (noteTags.isNotEmpty()) {
                    val allNoteIds = (notes.map { it.id } + proxy.dao().getSyncNotes().map { it.id }).toSet()
                    val allTagIds = (tags.map { it.id } + proxy.tagDao().getAllTagsSync().map { it.id }).toSet()
                    val validCrossRefs = noteTags.filter { it.noteId in allNoteIds && it.tagId in allTagIds }
                    if (validCrossRefs.isNotEmpty()) {
                        upsertNoteTagCrossRefs(validCrossRefs)
                    }
                }
            } else if (jsonElement.isJsonArray) {
                // Legacy format — plain array of NoteModel
                val notes = gson.fromJson(jsonElement.asJsonArray, Array<NoteModel>::class.java)?.toList() ?: emptyList()
                proxy.dao().upsertNotes(notes)
                // Tags remain empty — notes become "uncategorized"
            } else {
                return@withContext Result.failure(Exception("Unexpected backup file format: expected JSON object or array"))
            }

            // Refresh widget after restore
            refresher.refresh(app)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Queries files from Google Drive within the appDataFolder.
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
     */
    suspend fun deleteFile(fileId: String, drive: Drive?): Result<Void?> = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = drive?.files()?.delete(fileId)?.execute()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getMetaData(fileName: String): File {
        return File().setMimeType(FILE_MIME_TYPE).setName(fileName)
    }

    private suspend fun upsertTags(tags: List<TagModel>) {
        proxy.tagDao().upsertTags(tags)
    }

    private suspend fun upsertNoteTagCrossRefs(crossRefs: List<NoteTagCrossRef>) {
        proxy.tagDao().upsertNoteTagCrossRefs(crossRefs)
    }
}