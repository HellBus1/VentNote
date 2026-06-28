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
import com.google.gson.JsonSyntaxException
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
    }

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
                val jsonString = Gson().toJson(payload)
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
     * If the legacy format is detected, notes are restored and tags default to empty.
     */
    suspend fun readFile(fileId: String, drive: Drive?): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val jsonString = drive?.files()?.get(fileId)?.executeMediaAsInputStream()?.use {
                it.bufferedReader().use { reader -> reader.readText() }
            } ?: return@withContext Result.failure(Exception("Empty file"))

            val gson = Gson()

            // Try new BackupPayload format first
            val payload: BackupPayload? = try {
                val parsed = gson.fromJson(jsonString, BackupPayload::class.java)
                // A valid payload must have a notes list; a JSON array parsed as this class will have null notes
                if (parsed?.notes != null) parsed else null
            } catch (_: JsonSyntaxException) {
                null
            }

            if (payload != null) {
                // New format — restore notes, tags, and note-tag cross refs
                proxy.dao().upsertNotes(payload.notes)
                if (payload.tags.isNotEmpty()) {
                    upsertTags(payload.tags)
                }
                if (payload.noteTags.isNotEmpty()) {
                    upsertNoteTagCrossRefs(payload.noteTags)
                }
            } else {
                // Legacy format — plain array of NoteModel
                val notes = gson.fromJson(jsonString, Array<NoteModel>::class.java)?.toList() ?: emptyList()
                proxy.dao().upsertNotes(notes)
                // Tags remain empty — notes become "uncategorized"
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
        tags.forEach { tag ->
            proxy.tagDao().insertTag(tag)
        }
    }

    private suspend fun upsertNoteTagCrossRefs(crossRefs: List<NoteTagCrossRef>) {
        proxy.tagDao().insertNoteTagCrossRefs(crossRefs)
    }
}