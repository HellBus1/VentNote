package com.digiventure.ventnote.module

import android.content.Context
import androidx.room.Room
import com.digiventure.ventnote.data.local.NoteDAO
import com.digiventure.ventnote.data.local.NoteDatabase
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
    fun dao(database: NoteDatabase): NoteDAO {
        return database.dao()
    }

    @Singleton
    @Provides
    fun noteDatabase(@ApplicationContext context: Context): NoteDatabase {
            return Room.databaseBuilder(
                context,
                NoteDatabase::class.java,
                "note_database"
            ).build()
        }
}