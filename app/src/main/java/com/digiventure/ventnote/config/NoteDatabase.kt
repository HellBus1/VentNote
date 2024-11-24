package com.digiventure.ventnote.config

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.data.persistence.NoteDAO
import com.digiventure.ventnote.data.persistence.NoteModel
import java.util.Date

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

@Database(
    entities = [NoteModel::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 1, to = 2)
    ]
)
@TypeConverters(DateConverters::class)
abstract class NoteDatabase: RoomDatabase() {
    abstract fun dao(): NoteDAO

    companion object{
        @Volatile
        private var instance: NoteDatabase? = null

        fun getInstance(context : Context): NoteDatabase {
            if (instance == null) {
                synchronized(this) {
                    instance = Room.databaseBuilder(
                        context,
                        NoteDatabase::class.java,
                        Constants.BACKUP_FILE_NAME
                    ).build()
                }
            }

            return instance!!
        }
    }
}