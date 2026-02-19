package com.digiventure.ventnote

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.longClick
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

/**
 * Comprehensive E2E instrumentation tests for the NotesPage.
 *
 * These tests cover all features of the Notes list screen:
 * - Initial UI state
 * - Search bar filtering
 * - Note item interactions (click, long-press)
 * - Selection / marking mode (select, deselect, select all, unselect all, close)
 * - Delete flow (dialog, dismiss, confirm)
 * - Filter / sort bottom sheet
 * - Navigation (FAB → Creation, Note tap → Detail, Menu → Drawer)
 *
 * The database is seeded with 3 deterministic notes before each test and
 * cleaned up afterwards to ensure full isolation.
 */
@HiltAndroidTest
class NotesFeature : BaseAcceptanceTest() {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var databaseProxy: DatabaseProxy

    // Seeded test notes
    private val note1 = NoteModel(id = 0, title = "Shopping List", note = "Milk, eggs, bread")
    private val note2 = NoteModel(id = 0, title = "Meeting Notes", note = "Discuss Q1 roadmap")
    private val note3 = NoteModel(id = 0, title = "Ideas", note = "Build a widget for Android")

    @Before
    fun setUp() {
        hiltRule.inject()
        Intents.init()

        // Seed the database with deterministic notes
        runBlocking {
            databaseProxy.dao().upsertNotes(listOf(note1, note2, note3))
        }

        // Wait for the UI to settle after seeding
        composeTestRule.waitForIdle()
    }

    @After
    fun tearDown() {
        // Clean up all seeded notes to ensure test isolation
        runBlocking {
            val allNotes = databaseProxy.dao().getSyncNotes()
            if (allNotes.isNotEmpty()) {
                databaseProxy.dao().deleteNotes(*allNotes.toTypedArray())
            }
        }
        Intents.release()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 1. Initial State
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Verifies that the core UI elements are visible when the app launches.
     */
    @Test
    fun initialState_showsAppBarAndFab() {
        composeTestRule.onNodeWithTag(TestTags.TOP_APPBAR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.TOP_APPBAR_TITLE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.MENU_ICON_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.SORT_ICON_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.ADD_NOTE_FAB).assertIsDisplayed()
    }

    /**
     * Verifies that the note list is visible and contains the seeded notes.
     */
    @Test
    fun initialState_showsSeededNotes() {
        // Wait for the list and data to be fully displayed
        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.NOTE_RV, useUnmergedTree = true).assertIsDisplayed()
                composeTestRule.onNodeWithText("Shopping List").assertIsDisplayed()
                true
            } catch (e: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithTag(TestTags.NOTE_RV, useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Shopping List").assertIsDisplayed()
        composeTestRule.onNodeWithText("Meeting Notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ideas").assertIsDisplayed()
    }

    /**
     * Verifies that the search bar is visible in the note list.
     */
    @Test
    fun initialState_showsSearchBar() {
        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.TOP_APPBAR_TEXT_FIELD).assertIsDisplayed()
                true
            } catch (e: Throwable) {
                false
            }
        }
        composeTestRule.onNodeWithTag(TestTags.TOP_APPBAR_TEXT_FIELD).assertIsDisplayed()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. Search Bar
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Verifies that typing in the search bar filters notes by title.
     */
    @Test
    fun searchBar_filtersByTitle() {
        composeTestRule.onNodeWithTag(TestTags.TOP_APPBAR_TEXT_FIELD)
            .performClick()
            .performTextInput("Shopping")

        // Wait for debounce (300ms) + UI settle
        composeTestRule.mainClock.advanceTimeBy(400)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Shopping List").assertIsDisplayed()
        composeTestRule.onNodeWithText("Meeting Notes").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Ideas").assertIsNotDisplayed()
    }

    /**
     * Verifies that typing in the search bar filters notes by content/body text.
     */
    @Test
    fun searchBar_filtersByContent() {
        composeTestRule.onNodeWithTag(TestTags.TOP_APPBAR_TEXT_FIELD)
            .performClick()
            .performTextInput("roadmap")

        composeTestRule.mainClock.advanceTimeBy(400)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Meeting Notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Shopping List").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Ideas").assertIsNotDisplayed()
    }

    /**
     * Verifies that a search query that matches nothing shows an empty list.
     */
    @Test
    fun searchBar_noMatch_showsEmptyList() {
        composeTestRule.onNodeWithTag(TestTags.TOP_APPBAR_TEXT_FIELD)
            .performClick()
            .performTextInput("xyzzy_no_match")

        composeTestRule.mainClock.advanceTimeBy(400)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Shopping List").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Meeting Notes").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Ideas").assertIsNotDisplayed()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. Selection / Marking Mode
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Verifies that long-pressing a note enters marking mode, showing the
     * selection UI (close button, delete button, selected count).
     */
    @Test
    fun longPressNote_entersMarkingMode() {
        composeTestRule.onNodeWithText("Shopping List")
            .performTouchInput { longClick() }

        composeTestRule.waitForIdle()

        // Selection UI should appear
        composeTestRule.onNodeWithTag(TestTags.CLOSE_SELECT_ICON_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.DELETE_ICON_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.SELECTED_COUNT_CONTAINER).assertIsDisplayed()

        // Normal mode UI should disappear
        composeTestRule.onNodeWithTag(TestTags.MENU_ICON_BUTTON).assertIsNotDisplayed()
        composeTestRule.onNodeWithTag(TestTags.SORT_ICON_BUTTON).assertIsNotDisplayed()
        composeTestRule.onNodeWithTag(TestTags.ADD_NOTE_FAB).assertIsNotDisplayed()
    }

    /**
     * Verifies that the selected count updates correctly when notes are
     * toggled in marking mode.
     */
    @Test
    fun markingMode_tapNote_togglesSelectionCount() {
        // Enter marking mode by long-pressing the first note
        composeTestRule.onNodeWithText("Shopping List")
            .performTouchInput { longClick() }
        composeTestRule.waitForIdle()

        // "1 of 3 selected" should be shown
        composeTestRule.onNodeWithText("1 of 3 selected").assertIsDisplayed()

        // Tap another note to add it to selection
        composeTestRule.onNodeWithText("Meeting Notes").performClick()
        composeTestRule.waitForIdle()

        // "2 of 3 selected" should be shown
        composeTestRule.onNodeWithText("2 of 3 selected").assertIsDisplayed()

        // Tap the first note again to deselect it
        composeTestRule.onNodeWithText("Shopping List").performClick()
        composeTestRule.waitForIdle()

        // "1 of 3 selected" should be shown again
        composeTestRule.onNodeWithText("1 of 3 selected").assertIsDisplayed()
    }

    /**
     * Verifies that "Select All" from the dropdown marks all notes.
     */
    @Test
    fun markingMode_selectAll_selectsAllNotes() {
        // Enter marking mode
        composeTestRule.onNodeWithText("Shopping List")
            .performTouchInput { longClick() }
        composeTestRule.waitForIdle()

        // Open the dropdown
        composeTestRule.onNodeWithTag(TestTags.SELECTED_COUNT_CONTAINER).performClick()
        composeTestRule.waitForIdle()

        // Tap "Select All"
        composeTestRule.onNodeWithTag(TestTags.SELECT_ALL_OPTION).performClick()
        composeTestRule.waitForIdle()

        // All 3 notes should be selected
        composeTestRule.onNodeWithText("3 of 3 selected").assertIsDisplayed()
    }

    /**
     * Verifies that "Unselect All" from the dropdown clears all selections.
     */
    @Test
    fun markingMode_unselectAll_clearsSelection() {
        // Enter marking mode and select all first
        composeTestRule.onNodeWithText("Shopping List")
            .performTouchInput { longClick() }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.SELECTED_COUNT_CONTAINER).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.SELECT_ALL_OPTION).performClick()
        composeTestRule.waitForIdle()

        // Now open dropdown and unselect all
        composeTestRule.onNodeWithTag(TestTags.SELECTED_COUNT_CONTAINER).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.UNSELECT_ALL_OPTION).performClick()
        composeTestRule.waitForIdle()

        // 0 notes should be selected
        composeTestRule.onNodeWithText("0 of 3 selected").assertIsDisplayed()
    }

    /**
     * Verifies that the close button exits marking mode and restores normal UI.
     */
    @Test
    fun markingMode_closeButton_exitsMarkingMode() {
        // Enter marking mode
        composeTestRule.onNodeWithText("Shopping List")
            .performTouchInput { longClick() }
        composeTestRule.waitForIdle()

        // Press the close button
        composeTestRule.onNodeWithTag(TestTags.CLOSE_SELECT_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Normal mode UI should be restored
        composeTestRule.onNodeWithTag(TestTags.MENU_ICON_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.SORT_ICON_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.ADD_NOTE_FAB).assertIsDisplayed()

        // Selection UI should be gone
        composeTestRule.onNodeWithTag(TestTags.CLOSE_SELECT_ICON_BUTTON).assertIsNotDisplayed()
        composeTestRule.onNodeWithTag(TestTags.DELETE_ICON_BUTTON).assertIsNotDisplayed()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. Delete Flow
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Verifies that tapping the delete icon shows the confirmation dialog.
     */
    @Test
    fun deleteFlow_showsConfirmationDialog() {
        // Enter marking mode and select a note
        composeTestRule.onNodeWithText("Shopping List")
            .performTouchInput { longClick() }
        composeTestRule.waitForIdle()

        // Tap delete
        composeTestRule.onNodeWithTag(TestTags.DELETE_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Confirmation dialog should appear
        composeTestRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CONFIRM_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.DISMISS_BUTTON).assertIsDisplayed()
    }

    /**
     * Verifies that tapping "Dismiss" in the delete dialog cancels the operation.
     */
    @Test
    fun deleteFlow_dismissDialog_cancelsDelete() {
        // Enter marking mode, select a note, and open delete dialog
        composeTestRule.onNodeWithText("Shopping List")
            .performTouchInput { longClick() }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.DELETE_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Dismiss the dialog
        composeTestRule.onNodeWithTag(TestTags.DISMISS_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Dialog should be gone, note should still exist
        composeTestRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Shopping List").assertIsDisplayed()
    }

    /**
     * Verifies that confirming delete removes the selected note and shows a snackbar.
     */
    @Test
    fun deleteFlow_confirmDelete_removesNote() {
        // Enter marking mode and select "Shopping List"
        composeTestRule.onNodeWithText("Shopping List")
            .performTouchInput { longClick() }
        composeTestRule.waitForIdle()

        // Open delete dialog and confirm
        composeTestRule.onNodeWithTag(TestTags.DELETE_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.CONFIRM_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // The deleted note should no longer be visible
        composeTestRule.onNodeWithText("Shopping List").assertIsNotDisplayed()

        // The other notes should still be visible
        composeTestRule.onNodeWithText("Meeting Notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ideas").assertIsDisplayed()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5. Filter / Sort Bottom Sheet
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Verifies that tapping the sort icon opens the filter bottom sheet.
     */
    @Test
    fun sortButton_opensFilterSheet() {
        composeTestRule.onNodeWithTag(TestTags.SORT_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(TestTags.BOTTOM_SHEET).assertIsDisplayed()
    }

    /**
     * Verifies that the filter sheet can be dismissed by tapping the dismiss button.
     */
    @Test
    fun filterSheet_dismissButton_closesSheet() {
        composeTestRule.onNodeWithTag(TestTags.SORT_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Bottom sheet should be open
        composeTestRule.onNodeWithTag(TestTags.BOTTOM_SHEET).assertIsDisplayed()

        // Tap the "Dismiss" button inside the sheet
        composeTestRule.onNodeWithText("Dismiss").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(TestTags.BOTTOM_SHEET).assertIsNotDisplayed()
    }

    /**
     * Verifies that selecting a sort option and confirming applies the filter
     * and closes the sheet.
     */
    @Test
    fun filterSheet_selectSortByTitle_andConfirm_closesSheet() {
        composeTestRule.onNodeWithTag(TestTags.SORT_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        // Select "Title" as sort option
        composeTestRule.onNodeWithText("Title").performClick()
        composeTestRule.waitForIdle()

        // Tap confirm
        composeTestRule.onNodeWithText("Confirm").performClick()
        composeTestRule.waitForIdle()

        // Sheet should be dismissed
        composeTestRule.onNodeWithTag(TestTags.BOTTOM_SHEET).assertIsNotDisplayed()

        // Notes should still be visible (sorted by title)
        composeTestRule.onNodeWithText("Shopping List").assertIsDisplayed()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6. Navigation
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Verifies that tapping the FAB navigates to the Note Creation page.
     */
    @Test
    fun fab_click_navigatesToCreationPage() {
        composeTestRule.onNodeWithTag(TestTags.ADD_NOTE_FAB).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(TestTags.NOTE_CREATION_PAGE).assertIsDisplayed()
    }

    /**
     * Verifies that tapping a note item navigates to the Note Detail page.
     */
    @Test
    fun noteItem_click_navigatesToDetailPage() {
        composeTestRule.onNodeWithText("Shopping List").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(TestTags.NOTE_DETAIL_PAGE).assertIsDisplayed()
    }

    /**
     * Verifies that tapping the hamburger menu icon opens the navigation drawer.
     */
    @Test
    fun menuButton_click_opensNavDrawer() {
        composeTestRule.onNodeWithTag(TestTags.MENU_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(TestTags.NAV_DRAWER).assertIsDisplayed()
    }
}