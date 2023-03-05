package com.digiventure.ventnote.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.*

object DateConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

@Database(entities = [NoteModel::class], version = 1, exportSchema = false)
@TypeConverters(DateConverters::class)
abstract class NoteDatabase: RoomDatabase() {
    abstract fun dao(): NoteDAO
}