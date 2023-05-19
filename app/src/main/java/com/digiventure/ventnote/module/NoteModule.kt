package com.digiventure.ventnote.module

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.digiventure.ventnote.data.google_api.FileInfo
import com.digiventure.ventnote.data.local.NoteDAO
import com.digiventure.ventnote.data.local.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File

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
    fun databasePath(@ApplicationContext context: Context): List<FileInfo> {
        return try {
            val database = "note_database"
            val databaseShm = "note_database-shm"
            val databaseWal = "note_database-wal"

            listOf(
                FileInfo(File(context.getDatabasePath(database).absolutePath), database, context.getDatabasePath(database).absolutePath),
                FileInfo(File(context.getDatabasePath(databaseShm).absolutePath), databaseShm, context.getDatabasePath(databaseShm).absolutePath),
                FileInfo(File(context.getDatabasePath(databaseWal).absolutePath), databaseWal, context.getDatabasePath(databaseWal).absolutePath)
            )
        } catch (e: Exception) {
            emptyList()
        }
    }
}