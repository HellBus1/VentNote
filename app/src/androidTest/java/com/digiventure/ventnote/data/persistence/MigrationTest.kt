package com.digiventure.ventnote.data.persistence

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.digiventure.ventnote.config.NoteDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        NoteDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        var db = helper.createDatabase(TEST_DB, 1)

        // db has schema version 1. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        db.execSQL("INSERT INTO note_table (title, note, created_at, updated_at) VALUES ('Title 1', 'Note 1', 123456789, 123456789)")

        // Prepare for the next version.
        db.close()

        // Re-open the database with version 2 and provide the manual migration.
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, NoteDatabase.MIGRATION_1_2)
    }

    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        var db = helper.createDatabase(TEST_DB, 2)

        // db has schema version 2. insert some data.
        db.execSQL("INSERT INTO note_table (title, note, created_at, updated_at) VALUES ('Title 2', 'Note 2', 987654321, 987654321)")

        db.close()

        // Re-open with version 3 and provide the manual migration.
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, NoteDatabase.MIGRATION_2_3)
    }
}
