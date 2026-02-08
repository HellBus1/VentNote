package com.digiventure.ventnote.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 3,
    exportSchema = true
)
@TypeConverters(DateConverters::class)
abstract class NoteDatabase: RoomDatabase() {
    abstract fun dao(): NoteDAO

    companion object{
        @Volatile
        private var instance: NoteDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // No changes between version 1 and 2
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_note_table_title` ON `note_table` (`title`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_note_table_created_at` ON `note_table` (`created_at`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_note_table_updated_at` ON `note_table` (`updated_at`)")
            }
        }

        fun getInstance(context : Context): NoteDatabase {
            if (instance == null) {
                synchronized(this) {
                    instance = Room.databaseBuilder(
                        context,
                        NoteDatabase::class.java,
                        Constants.BACKUP_FILE_NAME
                    )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                }
            }

            return instance!!
        }
    }
}