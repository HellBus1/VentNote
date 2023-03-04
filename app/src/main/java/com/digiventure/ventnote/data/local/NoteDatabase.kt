package com.digiventure.ventnote.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.digiventure.ventnote.data.NoteModel

@Database(entities = [NoteModel::class], version = 1)
abstract class NoteDatabase: RoomDatabase() {
    abstract fun dao(): NoteDAO
}