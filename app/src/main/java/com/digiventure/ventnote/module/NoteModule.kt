package com.digiventure.ventnote.module

import android.content.Context
import androidx.room.Room
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.config.NoteDatabase
import com.digiventure.ventnote.data.persistence.NoteDAO
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
                Constants.DATABASE_NAME
            ).build()
        }
}