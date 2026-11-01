package com.digiventure.ventnote

import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.compose.setContent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import com.digiventure.utils.BaseAcceptanceTest
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.NoteTagCrossRef
import com.digiventure.ventnote.data.persistence.TagModel
import com.digiventure.ventnote.feature.backup.BackupPage
import com.digiventure.ventnote.feature.backup.viewmodel.AuthBaseVM
import com.digiventure.ventnote.feature.backup.viewmodel.AuthVM
import com.digiventure.ventnote.feature.backup.viewmodel.BackupPageBaseVM
import com.digiventure.ventnote.feature.backup.viewmodel.BackupPageVM
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import com.digiventure.ventnote.ui.theme.components.VentNoteTheme
import com.google.api.services.drive.model.File
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import java.io.FileOutputStream
import java.util.Date
import javax.inject.Inject

@HiltAndroidTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ScreenshotsCaptureTest : BaseAcceptanceTest() {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var databaseProxy: DatabaseProxy

    @Before
    fun setUp() {
        hiltRule.inject()
        runBlocking {
            val tags = listOf(
                TagModel(id = 1, name = "Work & Career", colorHex = "#90CAF9"),
                TagModel(id = 2, name = "Personal & Health", colorHex = "#A5D6A7"),
                TagModel(id = 3, name = "Finance & Money", colorHex = "#FFE082"),
                TagModel(id = 4, name = "Engineering & Code", colorHex = "#CE93D8"),
                TagModel(id = 9, name = "Urgent & Action", colorHex = "#EF9A9A")
            )
            val notes = listOf(
                NoteModel(
                    id = 1,
                    title = "Emergency Contacts & Medical Info",
                    note = "### Personal Details\n- Blood Type: **O Positive**\n- Allergies: Penicillin (Severe)",
                    createdAt = Date(1789016400000),
                    updatedAt = Date(1789020000000),
                    isPinned = true
                ),
                NoteModel(
                    id = 2,
                    title = "Financial Allocation & Budget Q4",
                    note = "Monthly Income Allocation Strategy (50/30/20 Rule):\n- **50% Fixed Essentials**",
                    createdAt = Date(1789621200000),
                    updatedAt = Date(1789628000000),
                    isPinned = false
                ),
                NoteModel(
                    id = 3,
                    title = "URGENT: Server SSL Certificate Renewal",
                    note = "## Action Required by EOD\n- Production SSL cert expires in 48 hours.",
                    createdAt = Date(1790226000000),
                    updatedAt = Date(1790233200000),
                    isPinned = true
                )
            )
            val crossRefs = listOf(
                NoteTagCrossRef(1, 2),
                NoteTagCrossRef(1, 9),
                NoteTagCrossRef(2, 3),
                NoteTagCrossRef(3, 1),
                NoteTagCrossRef(3, 4)
            )
            databaseProxy.tagDao().upsertTags(tags)
            databaseProxy.dao().upsertNotes(notes)
            databaseProxy.tagDao().upsertNoteTagCrossRefs(crossRefs)
        }

        composeTestRule.waitUntil(15000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.NOTES_PAGE).assertIsDisplayed()
                true
            } catch (_: Throwable) {
                false
            }
        }
    }

    private fun saveScreenshot(fileName: String) {
        val bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        val dir = InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null)
        val file = java.io.File(dir, fileName)
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        try {
            val sdFile = java.io.File("/sdcard/Download", fileName)
            FileOutputStream(sdFile).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        } catch (_: Exception) {}
    }

    @Test
    fun test1_captureFilterBottomSheet() {
        composeTestRule.onNodeWithTag(TestTags.SORT_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.BOTTOM_SHEET).assertIsDisplayed()
                true
            } catch (_: Throwable) {
                false
            }
        }
        Thread.sleep(1200)
        saveScreenshot("01_filter_bottomsheet.png")
    }

    @Test
    fun test2_captureSideBarChangeTheme() {
        composeTestRule.onNodeWithTag(TestTags.MENU_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.THEME_TILE).assertIsDisplayed()
                true
            } catch (_: Throwable) {
                false
            }
        }
        Thread.sleep(1200)
        saveScreenshot("02_sidebar_change_theme.png")
    }

    @Test
    fun test3_captureGoogleDriveList() {
        val dummyFiles = listOf(
            File().setId("1").setName("backup_2026-11-01 10:00:00.db"),
            File().setId("2").setName("backup_2026-10-31 18:30:00.db"),
            File().setId("3").setName("backup_2026-10-30 09:15:00.db"),
            File().setId("4").setName("backup_2026-10-29 14:45:00.db")
        )
        val mockVM = object : ViewModel(), BackupPageBaseVM {
            override val uiState: State<BackupPageVM.BackupPageState> = mutableStateOf(
                BackupPageVM.BackupPageState(
                    listOfBackupFileState = BackupPageVM.FileBackupListState.FileBackupListFinished
                )
            )
            override val driveBackupFileList: LiveData<List<File>> = MutableLiveData(dummyFiles)
            override fun backupDatabase() {}
            override fun restoreDatabase(fileId: String) {}
            override fun getBackupFileList() {}
            override fun deleteDatabase(fileId: String) {}
            override fun clearBackupFileList() {}
        }
        val mockAuthVM = object : ViewModel(), AuthBaseVM {
            override val uiState: State<AuthVM.AuthPageState> = mutableStateOf(
                AuthVM.AuthPageState(authState = AuthVM.AuthState.SignedIn)
            )
            override val eventFlow: SharedFlow<AuthVM.AuthState> = MutableSharedFlow()
            override fun signOut(onCompleteSignOutCallback: () -> Unit) {}
            override fun getSignInIntent(): Intent = Intent()
            override fun checkAuthState() {}
        }

        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.setContent {
                VentNoteTheme {
                    BackupPage(
                        navHostController = rememberNavController(),
                        authViewModel = mockAuthVM,
                        backupPageVM = mockVM
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        Thread.sleep(1200)
        saveScreenshot("03_google_drive_list.png")
    }

    @Test
    fun test4_captureAddNoteUi() {
        composeTestRule.onNodeWithTag(TestTags.ADD_NOTE_FAB).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNodeWithText("Add New Note").assertIsDisplayed()
                true
            } catch (_: Throwable) {
                false
            }
        }
        Thread.sleep(1200)
        saveScreenshot("add_note_ui.png")
    }
}
