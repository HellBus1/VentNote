package com.digiventure.ventnote

import com.digiventure.utils.BaseAcceptanceTest
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.NoteTagCrossRef
import com.digiventure.ventnote.data.persistence.TagModel
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import java.util.Date
import javax.inject.Inject

@HiltAndroidTest
class DatabaseSeederTest : BaseAcceptanceTest() {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var databaseProxy: DatabaseProxy

    @Test
    fun seedDatabaseWith7Notes() {
        hiltRule.inject()

        val tags = listOf(
            TagModel(id = 1, name = "Work & Career", colorHex = "#90CAF9"),
            TagModel(id = 2, name = "Personal & Health", colorHex = "#A5D6A7"),
            TagModel(id = 3, name = "Finance & Money", colorHex = "#FFE082"),
            TagModel(id = 4, name = "Engineering & Code", colorHex = "#CE93D8"),
            TagModel(id = 5, name = "Shopping & Errands", colorHex = "#FFCC80"),
            TagModel(id = 6, name = "Travel & Adventure", colorHex = "#80DEEA"),
            TagModel(id = 7, name = "Reading & Books", colorHex = "#B39DDB"),
            TagModel(id = 8, name = "Home & Living", colorHex = "#80CBC4"),
            TagModel(id = 9, name = "Urgent & Action", colorHex = "#EF9A9A"),
            TagModel(id = 10, name = "Ideas & Brainstorm", colorHex = "#F48FB1"),
            TagModel(id = 11, name = "Fitness & Training", colorHex = "#FFAB91"),
            TagModel(id = 12, name = "Recipes & Food", colorHex = "#C8E6C9")
        )

        val notes = listOf(
            NoteModel(
                id = 1,
                title = "Architecture RFC: Modular Navigation Flow",
                note = "Proposal to decouple feature modules by introducing a centralized Navigation Router.\n\n```kotlin\ninterface FeatureNavigator {\n    fun openNoteDetail(navController: NavHostController, noteId: Int)\n    fun openTagPicker(navController: NavHostController)\n}\n```\n\n**Benefits:**\n1. Eliminates direct screen imports between circular feature packages.\n2. Simplifies unit testing with navigation mocks.",
                createdAt = Date(1774674000000),
                updatedAt = Date(1774678000000),
                isPinned = false
            ),
            NoteModel(
                id = 2,
                title = "Book Notes: Designing Data-Intensive Applications",
                note = "Key takeaways from Chapter 3 (Storage and Retrieval):\n- **SSTables & LSM-Trees:** Sequential write throughput is vastly superior to random writes on both spinning disks and SSDs.\n- **B-Trees:** The standard for relational databases (like SQLite and PostgreSQL), offering predictable O(log N) lookup times.\n- **WAL (Write-Ahead Logging):** SQLite uses WAL mode to enable concurrent readers while a write transaction commits.",
                createdAt = Date(1785042000000),
                updatedAt = Date(1785045000000),
                isPinned = false
            ),
            NoteModel(
                id = 3,
                title = "Doctor Consultation & Prescription Directions",
                note = "Annual Health Checkup Notes with Dr. Aris:\n- Blood pressure: 118/76 (Normal)\n- Fasting Glucose: 92 mg/dL (Healthy range)\n- Vitamin D levels slightly below optimal: Take 2,000 IU Vitamin D3 daily with breakfast.\n- Follow-up lipid panel scheduled in 6 months.",
                createdAt = Date(1787634000000),
                updatedAt = Date(1787638000000),
                isPinned = false
            ),
            NoteModel(
                id = 4,
                title = "Emergency Contacts & Medical Info",
                note = "### Personal Details\n- Blood Type: **O Positive**\n- Allergies: Penicillin (Severe)\n\n### Emergency Contacts:\n1. Dr. Sarah Chen (Primary Physician): +1-555-0192\n2. Metro General Hospital Urgent Line: +1-555-0100\n3. Insurance Policy ID: #VENT-882910-A (Allianz Care)",
                createdAt = Date(1789016400000),
                updatedAt = Date(1789020000000),
                isPinned = true
            ),
            NoteModel(
                id = 5,
                title = "Financial Allocation & Budget Q4",
                note = "Monthly Income Allocation Strategy (50/30/20 Rule):\n- **50% Fixed Essentials:** Rent, utilities, high-speed fiber internet, groceries.\n- **30% Discretionary:** Dining out, subscriptions (JetBrains, Spotify, Gym), weekend trips.\n- **20% Long-Term Growth:** Index funds (Vanguard S&P 500 ETF) + High-Yield Savings emergency reserve.",
                createdAt = Date(1789621200000),
                updatedAt = Date(1789628000000),
                isPinned = false
            ),
            NoteModel(
                id = 6,
                title = "Sprint Retrospective: 1.4.0 Release",
                note = "### What Went Well\n- Jetpack Compose M3 migration completed with zero visual regressions.\n- Room SQLite Many-to-Many tag association reduced lookup latency.\n- Google Drive backup now retains historical timestamps cleanly.\n\n### What Needs Improvement\n- Tablet drawer layout was overstretching before clamping to 320dp.\n- Need automated UI integration tests for TagChipBar horizontal drag.",
                createdAt = Date(1789966800000),
                updatedAt = Date(1789972000000),
                isPinned = false
            ),
            NoteModel(
                id = 7,
                title = "URGENT: Server SSL Certificate Renewal",
                note = "## Action Required by EOD\n- Production SSL cert for api.ventnote.internal expires in 48 hours.\n- Coordinate with DevOps (Dmitri) to trigger Let's Encrypt renewal script.\n- Verify Nginx reload on reverse proxy without socket dropping.\n- Check mobile client pinning configuration to prevent handshake failures.",
                createdAt = Date(1790226000000),
                updatedAt = Date(1790233200000),
                isPinned = true
            )
        )

        val crossRefs = listOf(
            NoteTagCrossRef(1, 4),
            NoteTagCrossRef(1, 10),
            NoteTagCrossRef(2, 7),
            NoteTagCrossRef(2, 4),
            NoteTagCrossRef(3, 2),
            NoteTagCrossRef(4, 2),
            NoteTagCrossRef(4, 9),
            NoteTagCrossRef(5, 3),
            NoteTagCrossRef(5, 1),
            NoteTagCrossRef(5, 8),
            NoteTagCrossRef(6, 1),
            NoteTagCrossRef(6, 4),
            NoteTagCrossRef(6, 10),
            NoteTagCrossRef(7, 1),
            NoteTagCrossRef(7, 9),
            NoteTagCrossRef(7, 4)
        )

        runBlocking {
            databaseProxy.tagDao().upsertTags(tags)
            databaseProxy.dao().upsertNotes(notes)
            databaseProxy.tagDao().upsertNoteTagCrossRefs(crossRefs)
        }
    }
}
