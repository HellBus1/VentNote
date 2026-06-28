package com.digiventure.ventnote

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import com.digiventure.utils.BaseAcceptanceTest
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class TagManagementFeature : BaseAcceptanceTest() {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var databaseProxy: DatabaseProxy

    @Before
    fun setUp() {
        hiltRule.inject()
        Intents.init()
        
        // Ensure clean state before tests
        runBlocking {
            databaseProxy.tagDao().clearAllNoteTagCrossRefs()
            databaseProxy.tagDao().clearAllTags()
            val notes = databaseProxy.dao().getSyncNotes()
            if (notes.isNotEmpty()) {
                databaseProxy.dao().deleteNotes(*notes.toTypedArray())
            }
        }
        
        // Wait for the main page to launch
        composeTestRule.waitUntil(15000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.NOTES_PAGE).assertIsDisplayed()
                true
            } catch (_: Throwable) {
                false
            }
        }
    }

    @After
    fun tearDown() {
        runBlocking {
            databaseProxy.tagDao().clearAllNoteTagCrossRefs()
            databaseProxy.tagDao().clearAllTags()
            val notes = databaseProxy.dao().getSyncNotes()
            if (notes.isNotEmpty()) {
                databaseProxy.dao().deleteNotes(*notes.toTypedArray())
            }
        }
        Intents.release()
    }

    @Test
    fun navigateToTagManager_andCreateTag_andVerifyInList() {
        // 1. Navigate to Tag Manager
        composeTestRule.onNodeWithTag(TestTags.MENU_ICON_BUTTON).performClick()
        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.TAGS_TILE).assertIsDisplayed()
                true
            } catch (_: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithTag(TestTags.TAGS_TILE).performClick()
        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.TAG_MANAGER_PAGE).assertIsDisplayed()
                true
            } catch (_: Throwable) {
                false
            }
        }

        // Verify we are on Manage Tags page
        composeTestRule.onNodeWithText("No tags yet").assertIsDisplayed()

        // 2. Click "New Tag"
        composeTestRule.onNodeWithContentDescription("Create tag").performClick()
        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.TAG_NAME_FIELD).assertIsDisplayed()
                true
            } catch (_: Throwable) {
                false
            }
        }

        // Fill tag name "Work"
        composeTestRule.onNodeWithTag(TestTags.TAG_NAME_FIELD).performTextInput("Work")
        
        // Select Blue color
        composeTestRule.onNodeWithContentDescription("Color: Blue").performClick()
        
        // Confirm create
        composeTestRule.onNodeWithText("Create").performClick()
        composeTestRule.waitForIdle()

        // Verify the tag "Work" exists in the list
        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNodeWithText("Work").assertIsDisplayed()
                true
            } catch (_: Throwable) {
                false
            }
        }
    }

    @Test
    fun createDuplicateTag_showsErrorSnackbar() {
        // Pre-create "Personal" tag directly in database
        runBlocking {
            databaseProxy.tagDao().insertTag(
                com.digiventure.ventnote.data.persistence.TagModel(id = 0, name = "Personal", colorHex = "#EF5350")
            )
        }
        composeTestRule.waitForIdle()

        // Navigate to Tag Manager
        composeTestRule.onNodeWithTag(TestTags.MENU_ICON_BUTTON).performClick()
        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.TAGS_TILE).assertIsDisplayed()
                true
            } catch (_: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithTag(TestTags.TAGS_TILE).performClick()
        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.TAG_MANAGER_PAGE).assertIsDisplayed()
                true
            } catch (_: Throwable) {
                false
            }
        }

        // Check it displays "Personal" tag in the list
        composeTestRule.onNodeWithText("Personal").assertIsDisplayed()

        // Attempt to create another "Personal" tag (case-insensitive "personal")
        composeTestRule.onNodeWithContentDescription("Create tag").performClick()
        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.TAG_NAME_FIELD).assertIsDisplayed()
                true
            } catch (_: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithTag(TestTags.TAG_NAME_FIELD).performTextInput("personal")
        composeTestRule.onNodeWithText("Create").performClick()
        
        // Error message snackbar should display: "Tag name already exists"
        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNodeWithText("Tag name already exists").assertIsDisplayed()
                true
            } catch (_: Throwable) {
                false
            }
        }
    }

    @Test
    fun assignTagToNote_andFilterListByTag() {
        // Pre-create tags in DB
        runBlocking {
            databaseProxy.tagDao().insertTag(
                com.digiventure.ventnote.data.persistence.TagModel(id = 101, name = "Urgent", colorHex = "#EC407A")
            )
            databaseProxy.tagDao().insertTag(
                com.digiventure.ventnote.data.persistence.TagModel(id = 102, name = "Normal", colorHex = "#26A69A")
            )
        }
        composeTestRule.waitForIdle()

        // Navigate to Note Creation
        composeTestRule.onNodeWithTag(TestTags.ADD_NOTE_FAB).performClick()
        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).assertIsDisplayed()
                true
            } catch (e: Throwable) {
                composeTestRule.onRoot().printToLog("TAG")
                false
            }
        }

        // Fill note title and body
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextInput("Critical Task")
        composeTestRule.onNodeWithTag(TestTags.BODY_TEXT_FIELD).performTextInput("This is urgent.")

        // Tap Add Tags trigger button
        composeTestRule.onNodeWithText("Add Tags").performClick()
        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNode(
                    hasTestTag("tag_chip_Urgent") and hasAnyAncestor(hasTestTag(TestTags.BOTTOM_SHEET))
                ).assertIsDisplayed()
                true
            } catch (_: Throwable) {
                false
            }
        }

        // Click "Urgent" chip inside bottom sheet to toggle selection
        composeTestRule.onNode(
            hasTestTag("tag_chip_Urgent") and hasAnyAncestor(hasTestTag(TestTags.BOTTOM_SHEET))
        ).performClick()
        
        // Dismiss bottom sheet by clicking outside (or sending back press)
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK)
        composeTestRule.waitForIdle()

        // Click save button
        composeTestRule.onNodeWithTag(TestTags.SAVE_ICON_BUTTON).performClick()
        
        // We should be back on Notes Page
        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.NOTES_PAGE).assertIsDisplayed()
                true
            } catch (_: Throwable) {
                false
            }
        }

        // Check that "Critical Task" note is displayed
        composeTestRule.onNodeWithText("Critical Task").assertIsDisplayed()

        // Filter: Click "Urgent" folder chip in TagChipBar
        composeTestRule.onNode(
            hasTestTag("tag_chip_Urgent") and hasClickAction()
        ).performClick()
        composeTestRule.waitForIdle()

        // Ensure "Critical Task" remains displayed
        composeTestRule.onNodeWithText("Critical Task").assertIsDisplayed()
    }
}
