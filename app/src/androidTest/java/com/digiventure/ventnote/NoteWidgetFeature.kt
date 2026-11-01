package com.digiventure.ventnote

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.digiventure.utils.BaseAcceptanceTest
import com.digiventure.ventnote.data.google_drive.BackupPayload
import com.digiventure.ventnote.data.google_drive.DateTypeAdapter
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.NoteTagCrossRef
import com.digiventure.ventnote.data.persistence.TagModel
import com.digiventure.ventnote.feature.widget.NoteWidgetFactory
import com.digiventure.ventnote.feature.widget.NoteWidgetProvider
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import com.digiventure.ventnote.navigation.NoteModelParamType
import com.google.gson.GsonBuilder
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date
import javax.inject.Inject

@HiltAndroidTest
class NoteWidgetFeature : BaseAcceptanceTest() {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var databaseProxy: DatabaseProxy

    private lateinit var context: Context

    @Before
    fun setUp() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
        
        // Clear database
        runBlocking {
            val allNotes = databaseProxy.dao().getSyncNotes()
            if (allNotes.isNotEmpty()) {
                databaseProxy.dao().deleteNotes(*allNotes.toTypedArray())
            }
        }
    }

    @Test
    fun widgetFactory_reflectsDatabaseState() = runBlocking {
        // 1. Initial empty state
        val factory = NoteWidgetFactory(context, databaseProxy)
        factory.onDataSetChanged()
        assertEquals(0, factory.getCount())

        // 2. Add some notes
        val notes = listOf(
            NoteModel(1, "Title 1", "Content 1", Date(), Date()),
            NoteModel(2, "Title 2", "Content 2", Date(), Date())
        )
        databaseProxy.dao().upsertNotes(notes)

        // 3. Refresh factory
        factory.onDataSetChanged()
        
        // 4. Verify count
        assertEquals(2, factory.getCount())

        // 5. Verify basic RemoteViews generation
        val view1 = factory.getViewAt(0)
        val view2 = factory.getViewAt(1)
        
        assertNotNull(view1)
        assertNotNull(view2)
        assertEquals(R.layout.note_widget_item, view1.layoutId)
        
        // Note: We cannot easily check RemoteViews content (text) without complex reflection
        // but checking the layoutId and packageName confirms the factory is producing the right items.
    }

    @Test
    fun widgetFactory_handlesOutOfBounds() = runBlocking {
        val factory = NoteWidgetFactory(context, databaseProxy)
        factory.onDataSetChanged()
        
        // Should return a placeholder or at least not crash
        val oobView = factory.getViewAt(100)
        assertNotNull(oobView)
    }

    @Test
    fun widgetProvider_refreshLogic_doesNotCrash() {
        // This verifies that the static refresh logic executes without exceptions in the instrumentation context
        NoteWidgetProvider.refreshWidgets(context)
    }

    @Test
    fun widgetFactory_ordersPinnedNotesFirst_andHandlesUntitledNotes() = runBlocking {
        val olderDate = Date(1700000000000)
        val newerDate = Date(1700005000000)

        val unpinnedNewer = NoteModel(id = 10, title = "Newer Unpinned", note = "**Bold**", createdAt = newerDate, updatedAt = newerDate, isPinned = false)
        val pinnedOlder = NoteModel(id = 20, title = "", note = "*Italic*", createdAt = olderDate, updatedAt = olderDate, isPinned = true)
        databaseProxy.dao().upsertNotes(listOf(unpinnedNewer, pinnedOlder))

        val factory = NoteWidgetFactory(context, databaseProxy)
        factory.onDataSetChanged()

        assertEquals(2, factory.getCount())
        // Pinned note (id = 20) should be first despite older createdAt
        assertEquals(20L, factory.getItemId(0))
        assertEquals(10L, factory.getItemId(1))

        // Verify RemoteViews creation for untitled & markdown note succeeds
        val pinnedView = factory.getViewAt(0)
        assertNotNull(pinnedView)
    }

    @Test
    fun gsonAndLocaleResources_workAfterBuildOptimization() {
        // Verify English locale string resources resolve under localeFilters += ["en"]
        assertEquals("Untitled", context.getString(R.string.untitled))
        assertTrue(context.getString(R.string.app_name).isNotBlank())

        // Verify Gson navigation & backup serialization without kotlinx-serialization
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()
        val fixedDate = Date(1700000000000)
        val note = NoteModel(id = 42, title = "Serialized Note", note = "Body", createdAt = fixedDate, updatedAt = fixedDate, isPinned = true)
        val parsedNavNote = NoteModelParamType().parseValue(gson.toJson(note))
        assertEquals(42, parsedNavNote.id)
        assertEquals("Serialized Note", parsedNavNote.title)

        val payload = BackupPayload(
            version = 1,
            notes = listOf(note),
            tags = listOf(TagModel(id = 1, name = "Work", colorHex = "#90CAF9")),
            noteTags = listOf(NoteTagCrossRef(noteId = 42, tagId = 1))
        )
        val roundTrip = gson.fromJson(gson.toJson(payload), BackupPayload::class.java)
        assertEquals(1, roundTrip.notes?.size)
        assertEquals(1, roundTrip.tags?.size)
        assertEquals(1, roundTrip.noteTags?.size)
    }
}
