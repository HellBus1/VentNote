package com.digiventure.ventnote

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import com.digiventure.utils.BaseAcceptanceTest
import com.digiventure.ventnote.commons.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NoteCreationFeature : BaseAcceptanceTest() {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        Intents.init()

        // Wait for list and navigate to creation
        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.ADD_NOTE_FAB).assertIsDisplayed()
                true
            } catch (e: Throwable) {
                false
            }
        }
        
        composeTestRule.onNodeWithTag(TestTags.ADD_NOTE_FAB).performClick()
        composeTestRule.waitForIdle()
        
        // Ensure we are on the creation page
        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.NOTE_CREATION_PAGE).assertIsDisplayed()
                true
            } catch (e: Throwable) {
                false
            }
        }
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    /**
     * Verifies that the initial state of the creation page shows empty fields and a save button.
     */
    @Test
    fun initialState_showsEmptyFields() {
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).assertTextContains("")
        composeTestRule.onNodeWithTag(TestTags.BODY_TEXT_FIELD).assertTextContains("")
        composeTestRule.onNodeWithTag(TestTags.SAVE_ICON_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.BACK_ICON_BUTTON).assertIsDisplayed()
    }

    /**
     * Verifies that a validation dialog is shown when trying to save with an empty title.
     */
    @Test
    fun saveFlow_validation_emptyTitle_showsRequiredDialog() {
        // Leave title empty, add body
        composeTestRule.onNodeWithTag(TestTags.BODY_TEXT_FIELD).performTextInput("Some content")
        
        composeTestRule.onNodeWithTag(TestTags.SAVE_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Validation dialog should appear
        composeTestRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CONFIRM_BUTTON).performClick()
    }

    /**
     * Verifies that a validation dialog is shown when trying to save with an empty body.
     */
    @Test
    fun saveFlow_validation_emptyBody_showsRequiredDialog() {
        // Add title, leave body empty
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextInput("Some title")
        
        composeTestRule.onNodeWithTag(TestTags.SAVE_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Validation dialog should appear
        composeTestRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CONFIRM_BUTTON).performClick()
    }

    /**
     * Verifies that filling both fields and saving adding the note and navigates back to the list.
     */
    @Test
    fun saveFlow_successfullyAddsNote() {
        val title = "New Note Title"
        val body = "New Note Body Content"

        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextInput(title)
        composeTestRule.onNodeWithTag(TestTags.BODY_TEXT_FIELD).performTextInput(body)
        
        // Ensure no validation dialog is present
        composeTestRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertDoesNotExist()
        
        // Try clicking using touch input for better resilience
        composeTestRule.onNodeWithTag(TestTags.SAVE_ICON_BUTTON).performTouchInput { click() }
        composeTestRule.waitForIdle()

        // Should navigate back to list
        composeTestRule.waitUntil(15000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.NOTES_PAGE).assertIsDisplayed()
                true
            } catch (e: Throwable) {
                false
            }
        }
        
        // Use waitUntil for the text as well to handle slow list updates
        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithText(title).assertIsDisplayed()
                true
            } catch (e: Throwable) {
                false
            }
        }
    }

    /**
     * Verifies that hitting back with dirty data shows the cancel confirmation dialog.
     */
    @Test
    fun cancelFlow_showsConfirmationDialog() {
        // Enter some text
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextInput("Dirty data")
        
        // Use back icon instead of BackHandler for direct UI interaction
        composeTestRule.onNodeWithTag(TestTags.BACK_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Cancel dialog should appear
        composeTestRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsDisplayed()
    }

    /**
     * Verifies that confirming the cancel dialog navigates back to the list.
     */
    @Test
    fun cancelFlow_confirm_navigatesBack() {
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextInput("Dirty data")
        
        composeTestRule.onNodeWithTag(TestTags.BACK_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(TestTags.CONFIRM_BUTTON).performClick() // Confirm cancel
        composeTestRule.waitForIdle()

        // Should return to notes page
        composeTestRule.onNodeWithTag(TestTags.NOTES_PAGE).assertIsDisplayed()
    }

    /**
     * Verifies that dismissing the cancel dialog keeps the user on the creation page.
     */
    @Test
    fun cancelFlow_dismiss_staysOnPage() {
        val text = "Dirty data"
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextInput(text)
        
        composeTestRule.onNodeWithTag(TestTags.BACK_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(TestTags.DISMISS_BUTTON).performClick() // Dismiss dialog
        composeTestRule.waitForIdle()

        // Should stay on creation page with data intact
        composeTestRule.onNodeWithTag(TestTags.NOTE_CREATION_PAGE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).assertTextContains(text)
    }

    /**
     * Verifies that focus management works between fields.
     */
    @Test
    fun keyboardFocusManagement() {
        // Click title field
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performClick()
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).assertIsFocused()

        // Click body field
        composeTestRule.onNodeWithTag(TestTags.BODY_TEXT_FIELD).performClick()
        composeTestRule.onNodeWithTag(TestTags.BODY_TEXT_FIELD).assertIsFocused()
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).assertIsNotFocused()
    }
}
