package com.digiventure.ventnote

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import com.digiventure.utils.BaseAcceptanceTest
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.data.persistence.NoteModel
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
class NoteDetailFeature : BaseAcceptanceTest() {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var databaseProxy: DatabaseProxy

    // Seeded test note
    private val testNote = NoteModel(id = 1, title = "Shopping List", note = "Milk, eggs, bread")

    @Before
    fun setUp() {
        hiltRule.inject()
        Intents.init()

        // Seed the database
        runBlocking {
            databaseProxy.dao().upsertNotes(listOf(testNote))
        }

        // Wait for list and navigate to detail
        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithText("Shopping List").assertIsDisplayed()
                true
            } catch (e: Throwable) {
                false
            }
        }
        
        composeTestRule.onNodeWithText("Shopping List").performClick()
        composeTestRule.waitForIdle()
        
        // Ensure we are on the detail page using robust wait
        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.NOTE_DETAIL_PAGE).assertIsDisplayed()
                true
            } catch (e: Throwable) {
                false
            }
        }
    }

    @After
    fun tearDown() {
        runBlocking {
            val allNotes = databaseProxy.dao().getSyncNotes()
            if (allNotes.isNotEmpty()) {
                databaseProxy.dao().deleteNotes(*allNotes.toTypedArray())
            }
        }
        Intents.release()
    }

    /**
     * Verifies that the note details (title and body) are correctly displayed.
     */
    @Test
    fun initialState_showsNoteDetails() {
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).assertTextContains("Shopping List")
        composeTestRule.onNodeWithTag(TestTags.BODY_TEXT_FIELD).assertTextContains("Milk, eggs, bread")
        
        composeTestRule.onNodeWithTag(TestTags.EDIT_ICON_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.DELETE_ICON_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.SHARE_ICON_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.BACK_ICON_BUTTON).assertIsDisplayed()
    }

    /**
     * Verifies that clicking the edit button changes the UI to editing mode.
     */
    @Test
    fun editMode_uiChanges() {
        composeTestRule.onNodeWithTag(TestTags.EDIT_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Save and Cancel buttons should be shown
        composeTestRule.onNodeWithTag(TestTags.SAVE_ICON_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CANCEL_ICON_BUTTON).assertIsDisplayed()

        // Edit and Delete should be hidden
        composeTestRule.onNodeWithTag(TestTags.EDIT_ICON_BUTTON).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.DELETE_ICON_BUTTON).assertDoesNotExist()
    }

    /**
     * Verifies that modifying the note and saving it updates the content and returns to view mode.
     */
    @Test
    fun saveFlow_updatesNote() {
        composeTestRule.onNodeWithTag(TestTags.EDIT_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Modify content
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextReplacement("Updated Title")
        composeTestRule.onNodeWithTag(TestTags.BODY_TEXT_FIELD).performTextReplacement("Updated Body")
        
        composeTestRule.onNodeWithTag(TestTags.SAVE_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Should return to view mode (Edit button reappears)
        composeTestRule.onNodeWithTag(TestTags.EDIT_ICON_BUTTON).assertIsDisplayed()
        
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).assertTextContains("Updated Title")
        composeTestRule.onNodeWithTag(TestTags.BODY_TEXT_FIELD).assertTextContains("Updated Body")
    }

    /**
     * Verifies that a validation dialog is shown when trying to save an empty title.
     */
    @Test
    fun saveFlow_validation_emptyTitle_showsRequiredDialog() {
        composeTestRule.onNodeWithTag(TestTags.EDIT_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Clear title
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextReplacement("")
        
        composeTestRule.onNodeWithTag(TestTags.SAVE_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Validation dialog should appear
        composeTestRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CONFIRM_BUTTON).performClick() // Dismiss it
    }

    /**
     * Verifies that a validation dialog is shown when trying to save an empty body.
     */
    @Test
    fun saveFlow_validation_emptyBody_showsRequiredDialog() {
        composeTestRule.onNodeWithTag(TestTags.EDIT_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Clear body
        composeTestRule.onNodeWithTag(TestTags.BODY_TEXT_FIELD).performTextReplacement("")
        
        composeTestRule.onNodeWithTag(TestTags.SAVE_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Validation dialog should appear
        composeTestRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CONFIRM_BUTTON).performClick() // Dismiss it
    }

    /**
     * Verifies that canceling an edit reverts changes to the original content.
     */
    @Test
    fun cancelFlow_revertsChanges() {
        composeTestRule.onNodeWithTag(TestTags.EDIT_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Modify content
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextReplacement("Dirty Title")
        
        composeTestRule.onNodeWithTag(TestTags.CANCEL_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Cancel dialog should appear
        composeTestRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CONFIRM_BUTTON).performClick() // Confirm cancel
        composeTestRule.waitForIdle()

        // Should return to original content and view mode
        composeTestRule.onNodeWithTag(TestTags.EDIT_ICON_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).assertTextContains("Shopping List")
    }

    /**
     * Verifies that dismissing the cancel dialog keeps the user in edit mode with dirty data.
     */
    @Test
    fun cancelFlow_dismissesDialog_staysInEditMode() {
        composeTestRule.onNodeWithTag(TestTags.EDIT_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextReplacement("Dirty Title")
        
        composeTestRule.onNodeWithTag(TestTags.CANCEL_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Cancel dialog should appear
        composeTestRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.DISMISS_BUTTON).performClick() // Dismiss dialog
        composeTestRule.waitForIdle()

        // Should stay in edit mode with dirty data
        composeTestRule.onNodeWithTag(TestTags.SAVE_ICON_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).assertTextContains("Dirty Title")
    }

    /**
     * Verifies that deleting a note removes it from the database and navigates back to the list.
     */
    @Test
    fun deleteFlow_removesNoteAndNavigatesBack() {
        composeTestRule.onNodeWithTag(TestTags.DELETE_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Confirmation dialog
        composeTestRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CONFIRM_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Should navigate back to list using robust wait
        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.NOTES_PAGE).assertIsDisplayed()
                true
            } catch (e: Throwable) {
                false
            }
        }
        
        composeTestRule.onNodeWithText("Shopping List").assertDoesNotExist()
    }

    /**
     * Verifies that dismissing the delete dialog keeps the user on the detail page.
     */
    @Test
    fun deleteFlow_dismissesDialog_staysOnDetail() {
        composeTestRule.onNodeWithTag(TestTags.DELETE_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Confirmation dialog
        composeTestRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.DISMISS_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Should stay on detail page
        composeTestRule.onNodeWithTag(TestTags.NOTE_DETAIL_PAGE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.DELETE_ICON_BUTTON).assertIsDisplayed()
    }

    /**
     * Verifies that clicking the share button navigates to the share preview page.
     */
    @Test
    fun shareFlow_navigatesToSharePage() {
        composeTestRule.onNodeWithTag(TestTags.SHARE_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Verify share page is displayed using robust wait
        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.SHARE_PAGE, useUnmergedTree = true).assertIsDisplayed()
                true
            } catch (e: Throwable) {
                false
            }
        }
    }

    /**
     * Verifies that clicking the back button returns the user to the notes list.
     */
    @Test
    fun backNavigation_returnsToNotesPage() {
        composeTestRule.onNodeWithTag(TestTags.BACK_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Verify notes page is displayed using robust wait
        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.NOTES_PAGE).assertIsDisplayed()
                true
            } catch (e: Throwable) {
                false
            }
        }
    }

    /**
     * Verifies that hitting back while in edit mode shows the cancel confirmation dialog.
     */
    @Test
    fun backNavigation_inEditMode_showsCancelDialog() {
        composeTestRule.onNodeWithTag(TestTags.EDIT_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Trigger BackHandler
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK)
        composeTestRule.waitForIdle()

        // Cancel dialog should appear
        composeTestRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CONFIRM_BUTTON).performClick() // Confirm cancel
        composeTestRule.waitForIdle()

        // Should return to original content and view mode (Edit button reappears)
        composeTestRule.onNodeWithTag(TestTags.EDIT_ICON_BUTTON).assertIsDisplayed()
    }

    /**
     * Verifies that focus management works in edit mode.
     */
    @Test
    fun editMode_keyboardInteractions() {
        composeTestRule.onNodeWithTag(TestTags.EDIT_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Click title field
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performClick()
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).assertIsFocused()

        // Click body field
        composeTestRule.onNodeWithTag(TestTags.BODY_TEXT_FIELD).performClick()
        composeTestRule.onNodeWithTag(TestTags.BODY_TEXT_FIELD).assertIsFocused()
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).assertIsNotFocused()
    }
}
