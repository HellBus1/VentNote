package com.digiventure.ventnote.data.remote

import android.util.Log
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File as DriveFile
import java.io.File

import javax.inject.Inject


class NoteRemoteService @Inject constructor(
//    private val drive: Drive,
    private val dbPath: NoteDatabasePathModel
) {
   suspend fun uploadDBtoDrive() {
//       val currentDB = File(dbPath.mainDatabasePath)
//
//       val storageFile = DriveFile()
//       storageFile.parents = listOf("VentNoteDataBackup")
//       storageFile.name = currentDB.name
//
//       Log.d("Path", currentDB.name)
//
//       if (currentDB.exists()) {
//           val mediaContent = FileContent("", currentDB)
//
//           val file = drive.files().create(storageFile, mediaContent).execute()
//           System.out.printf("Filename: %s File ID: %s \n", file.name, file.id)
//       }
   }
}