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
import com.digiventure.ventnote.data.persistence.NoteTagCrossRef
import com.digiventure.ventnote.data.persistence.TagDAO
import com.digiventure.ventnote.data.persistence.TagModel
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
    entities = [NoteModel::class, TagModel::class, NoteTagCrossRef::class],
    version = 5,
    exportSchema = true
)
@TypeConverters(DateConverters::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun dao(): NoteDAO
    abstract fun tagDao(): TagDAO

    companion object {
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

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create tag_table
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `tag_table` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`name` TEXT NOT NULL, " +
                    "`color_hex` TEXT NOT NULL)"
                )
                // Create note_tag_table junction table
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `note_tag_table` (" +
                    "`noteId` INTEGER NOT NULL, " +
                    "`tagId` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`noteId`, `tagId`), " +
                    "FOREIGN KEY(`noteId`) REFERENCES `note_table`(`id`) ON DELETE CASCADE, " +
                    "FOREIGN KEY(`tagId`) REFERENCES `tag_table`(`id`) ON DELETE CASCADE)"
                )
                // Create index on tagId for performant tag-based queries
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_note_tag_table_tagId` ON `note_tag_table` (`tagId`)"
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add is_pinned column; existing notes default to 0 (false)
                db.execSQL(
                    "ALTER TABLE `note_table` ADD COLUMN `is_pinned` INTEGER NOT NULL DEFAULT 0"
                )
                // Index for fast pinned-first ORDER BY
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_note_table_is_pinned` ON `note_table` (`is_pinned`)"
                )
            }
        }

        fun getInstance(context: Context): NoteDatabase {
            if (instance == null) {
                synchronized(this) {
                    instance = Room.databaseBuilder(
                        context,
                        NoteDatabase::class.java,
                        Constants.BACKUP_FILE_NAME
                    )
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                        .build()
                }
            }
            return instance!!
        }
    }
}