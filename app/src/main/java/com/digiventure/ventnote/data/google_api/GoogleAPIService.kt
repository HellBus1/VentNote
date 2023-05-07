package com.digiventure.ventnote.data.google_api

import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.FileContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import com.google.api.services.drive.model.File as DriveFile


class GoogleAPIService @Inject constructor(
    private val dbPath: NoteDatabasePathModel,
) {
    suspend fun uploadDBtoDrive(credential: GoogleAccountCredential) = withContext(Dispatchers.IO) {
        val googleApiHelper = GoogleApiHelper(credential)
        val currentDB = File(dbPath.mainDatabasePath)

        val gFolder = DriveFile()
        gFolder.name = "VentNoteDataBackup"
        gFolder.mimeType = "application/vnd.google-apps.folder"

        val drive = googleApiHelper.driveInstance

        if (drive != null) {
            try {
                // check if backup folder exists
                val folderQuery = drive.files().list().setQ("mimeType='application/vnd.google-apps.folder' and trashed=false and name='${gFolder.name}'")
                var folderList = folderQuery.execute().files

                if (folderList.isEmpty()) {
                    // create the backup folder if it does not exist
                    val folder = drive.files().create(gFolder).setFields("id").execute()
                    folderList = listOf(folder)
                }

                // set the parent folder of the backup file
                val storageFile = DriveFile()
                storageFile.parents = listOf(folderList[0].id)
                storageFile.name = currentDB.name

                if (currentDB.exists()) {
                    val mediaContent = FileContent("application/x-sqlite3", currentDB)
                    val uploadDB = drive.files().create(storageFile, mediaContent).execute()
                    Log.d("VentNote", "Filename: ${uploadDB.name}, File ID: ${uploadDB.id}")
                } else {
                    Log.e("VentNote", "Room database file does not exist")
                }
            } catch (e: GoogleJsonResponseException) {
                Log.e("VentNote", "Unable to upload file: ${e.details}")
                throw e
            } catch (e: Exception) {
                Log.e("VentNote", "Error uploading file: ${e.message}")
                throw e
            }
        } else {
            Log.e("VentNote", "Drive instance is null")
        }
    }
}