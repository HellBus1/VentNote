package com.digiventure.ventnote.module

import android.content.Context
import androidx.room.Room
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.config.DriveAPI
import com.digiventure.ventnote.config.NoteDatabase
import com.digiventure.ventnote.data.persistence.NoteDAO
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.services.drive.Drive
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NoteModule {
    @Singleton
    @Provides
    fun dao(database: NoteDatabase): NoteDAO = database.dao()

    @Singleton
    @Provides
    fun noteDatabase(@ApplicationContext context: Context): NoteDatabase =
        Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun googleDriveInstance(@ApplicationContext context: Context): Drive? =
        GoogleSignIn.getLastSignedInAccount(context)?.run {
            DriveAPI.getInstance(context, this)
        }
}