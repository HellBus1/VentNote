package com.digiventure.ventnote.module

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.digiventure.ventnote.data.google_api.DatabaseFiles
import com.digiventure.ventnote.data.local.NoteDAO
import com.digiventure.ventnote.data.local.NoteDatabase
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
    fun sharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("note_shared_preference", Context.MODE_PRIVATE)
    }

    @Provides
    fun databasePath(@ApplicationContext context: Context): DatabaseFiles {
        return DatabaseFiles(
            database = context.getDatabasePath("note_database").absolutePath,
            databaseShm = context.getDatabasePath("note_database-shm").absolutePath,
            databaseWal = context.getDatabasePath("note_database-wal").absolutePath
        )
    }
}