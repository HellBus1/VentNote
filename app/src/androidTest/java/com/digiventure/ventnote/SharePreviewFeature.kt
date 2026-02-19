package com.digiventure.ventnote

import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import com.digiventure.utils.BaseAcceptanceTest
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject

@HiltAndroidTest
class SharePreviewFeature : BaseAcceptanceTest() {

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
        
        // Seed a note for sharing with a fixed date
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val testDate = sdf.parse("2026-02-19T10:00:00Z")!!

        runBlocking {
            databaseProxy.dao().upsertNotes(listOf(
                NoteModel(1, "Test Title", "Test Note Content", testDate, testDate)
            ))
        }

        // Wait for list and navigate to detail -> Share Preview
        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithText("Test Title").assertIsDisplayed()
                true
            } catch (e: Throwable) {
                false
            }
        }
        
        composeTestRule.onNodeWithText("Test Title").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(TestTags.SHARE_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithTag(TestTags.SHARE_PAGE, useUnmergedTree = true).assertIsDisplayed()
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

    @Test
    fun initialState_showsNoteContent() {
        // Verify Title and Body are displayed (Date format might vary so we check tag existence)
        composeTestRule.onNodeWithTag(TestTags.TITLE_TEXT).assertTextContains("Test Title")
        composeTestRule.onNodeWithTag(TestTags.BODY_TEXT).assertTextContains("Test Note Content")
        composeTestRule.onNodeWithTag(TestTags.DATE_TEXT).assertIsDisplayed()
    }

    @Test
    fun helpDialog_visibility() {
        composeTestRule.onNodeWithTag(TestTags.HELP_ICON_BUTTON).performClick()
        composeTestRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertIsDisplayed()
        
        composeTestRule.onNodeWithTag(TestTags.DISMISS_BUTTON).performClick()
        composeTestRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertDoesNotExist()
    }

    @Test
    fun shareTrigger_launchesIntent() {
        // Click the share button in bottom bar
        composeTestRule.onNodeWithTag(TestTags.SHARE_ICON_BUTTON).performClick()

        // Verify Share Sheet is displayed (Wait for it if needed, but it's usually immediate)
        composeTestRule.onNodeWithText("Share Note").assertIsDisplayed()
        
        // Click Share in the bottom sheet using its text
        composeTestRule.onNodeWithText("Share Note as Text").performClick()

        // Verify intent was sent
        intended(allOf(
            hasAction(Intent.ACTION_CHOOSER),
            hasExtra(`is`(Intent.EXTRA_INTENT), allOf(
                hasAction(Intent.ACTION_SEND),
                hasExtra(`is`(Intent.EXTRA_TEXT), allOf(
                    containsString("Test Title"),
                    containsString("Test Note Content")
                ))
            ))
        ))
    }

    @Test
    fun backNavigation_returnsToDetail() {
        // Using System Back via NavController or Hardware back
        composeTestRule.onNodeWithTag(TestTags.BACK_ICON_BUTTON).performClick()
        composeTestRule.onNodeWithTag(TestTags.NOTE_DETAIL_PAGE).assertIsDisplayed()
    }
}
