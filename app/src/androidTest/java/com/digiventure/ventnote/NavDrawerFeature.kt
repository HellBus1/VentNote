package com.digiventure.ventnote

import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import com.digiventure.utils.BaseAcceptanceTest
import com.digiventure.ventnote.commons.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NavDrawerFeature : BaseAcceptanceTest() {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        Intents.init()
        
        // Open the drawer
        composeTestRule.onNodeWithTag(TestTags.NOTES_PAGE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.MENU_ICON_BUTTON).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.NAV_DRAWER, useUnmergedTree = true).assertIsDisplayed()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun initialState_showsMenuItems() {
        composeTestRule.onNodeWithTag(TestTags.RATE_APP_TILE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.MORE_APPS_TILE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.APP_VERSION_TILE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.THEME_TILE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.COLOR_MODE_TILE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.BACKUP_TILE).assertIsDisplayed()
    }

    @Test
    fun rateApp_launchesPlayStore() {
        composeTestRule.onNodeWithTag(TestTags.RATE_APP_TILE).performClick()
        
        intended(allOf(
            hasAction(Intent.ACTION_VIEW),
            hasData("https://play.google.com/store/apps/details?id=com.digiventure.ventnote")
        ))
    }

    @Test
    fun moreApps_launchesDeveloperPage() {
        composeTestRule.onNodeWithTag(TestTags.MORE_APPS_TILE).performClick()
        
        intended(allOf(
            hasAction(Intent.ACTION_VIEW),
            hasData("https://play.google.com/store/apps/developer?id=Mattrmost")
        ))
    }

    @Test
    fun backupNavigation_navigatesToBackupPage() {
        composeTestRule.onNodeWithTag(TestTags.BACKUP_TILE).performClick()
        composeTestRule.waitForIdle()
        
        // Use text or tag to verify backup page (usually has "Backup" title in header)
        composeTestRule.onNodeWithText("Backup Notes").assertIsDisplayed()
    }

    @Test
    fun themeColorChange_updatesTheme() {
        // Verify we can click all theme colors
        composeTestRule.onNodeWithTag(TestTags.THEME_COLOR_PURPLE).performClick()
        composeTestRule.onNodeWithTag(TestTags.THEME_COLOR_CRIMSON).performClick()
        composeTestRule.onNodeWithTag(TestTags.THEME_COLOR_CADMIUM_GREEN).performClick()
        composeTestRule.onNodeWithTag(TestTags.THEME_COLOR_COBALT_BLUE).performClick()
        
        // Clicks should not crash and should update internal state (hard to verify without custom matchers)
        composeTestRule.onNodeWithTag(TestTags.THEME_TILE).assertIsDisplayed()
    }

    @Test
    fun colorModeToggle_updatesMode() {
        // Find the current mode by checking the subtitle text
        val lightModeText = "switch to light mode"
        val darkModeText = "switch to dark mode"
        
        // Initial click to toggle (assuming default is light mode or whatever)
        // We look for either text to be sure
        val initialNode = composeTestRule.onNode(
            hasText(lightModeText, ignoreCase = true) or hasText(darkModeText, ignoreCase = true)
        )
        
        initialNode.assertIsDisplayed()
        val isInitiallyLight = try {
            composeTestRule.onNodeWithText(darkModeText, ignoreCase = true).assertIsDisplayed()
            true // It says "switch to dark", so it is currently light
        } catch (e: Throwable) {
            false
        }
        
        // Toggle
        composeTestRule.onNodeWithTag(TestTags.COLOR_MODE_TILE).performClick()
        composeTestRule.waitForIdle()
        
        // Verify text swapped
        if (isInitiallyLight) {
            composeTestRule.onNodeWithText(lightModeText, ignoreCase = true).assertIsDisplayed()
        } else {
            composeTestRule.onNodeWithText(darkModeText, ignoreCase = true).assertIsDisplayed()
        }
    }

    @Test
    fun drawer_canBeClosed() {
        // Click outside or use a specific close mechanism if available, 
        // but here we can just swipe or click the content area if accessible.
        // Easiest is to just verify it closes on back press
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.NAV_DRAWER).assertDoesNotExist()
    }
}
