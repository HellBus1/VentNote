package com.digiventure.ventnote.module

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.digiventure.ventnote.data.local.NoteDAO
import com.digiventure.ventnote.data.local.NoteDatabase
import com.digiventure.ventnote.data.remote.NoteDatabasePathModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class NoteModule {
    @Provides
    fun dao(database: NoteDatabase): NoteDAO {
        return database.dao()
    }

    @Provides
    fun noteDatabase(@ApplicationContext context: Context): NoteDatabase {
            return Room.databaseBuilder(
                context,
                NoteDatabase::class.java,
                "note_database"
            ).fallbackToDestructiveMigration().build()
        }

    @Provides
    fun googleAccountCredential(@ApplicationContext context: Context): GoogleSignInClient {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE), Scope(DriveScopes.DRIVE))
            .build()

        return GoogleSignIn.getClient(context, signInOptions)
    }

//    @Provides
//    fun driveBuilder(@ApplicationContext context: Context): Drive {
//        return Drive.Builder(
//            AndroidHttp.newCompatibleTransport(),
//            GsonFactory(),
//            credential
//        )
//            .setApplicationName("VentNote")
//            .build()
//    }

    @Provides
    fun databasePath(@ApplicationContext context: Context): NoteDatabasePathModel {
        return NoteDatabasePathModel(context.getDatabasePath("note_database.db").absolutePath)
    }
}