package com.digiventure.ventnote

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import com.digiventure.ventnote.commons.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NotesFeature {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var composeTestRule = createAndroidComposeRule(MainActivity::class.java)

    // Variables related to TopAppBar
    private lateinit var topAppBar: SemanticsNodeInteraction
    private lateinit var searchIconButton: SemanticsNodeInteraction
    private lateinit var menuIconButton: SemanticsNodeInteraction
    private lateinit var topAppBarTitle: SemanticsNodeInteraction
    private lateinit var topAppBarTextField: SemanticsNodeInteraction
    private lateinit var closeSearchIconButton: SemanticsNodeInteraction

    private lateinit var navDrawer: SemanticsNodeInteraction
    private lateinit var rateAppTile: SemanticsNodeInteraction

    @Before
    fun setUp() {
        hiltRule.inject()
        Intents.init()

        // Initialize all widgets
        topAppBar = composeTestRule.onNodeWithTag(TestTags.TOP_APPBAR)
        searchIconButton = composeTestRule.onNodeWithTag(TestTags.SEARCH_ICON_BUTTON)
        menuIconButton = composeTestRule.onNodeWithTag(TestTags.MENU_ICON_BUTTON)
        topAppBarTitle = composeTestRule.onNodeWithTag(TestTags.TOP_APPBAR_TITLE)
        topAppBarTextField = composeTestRule.onNodeWithTag(TestTags.TOP_APPBAR_TEXTFIELD)
        closeSearchIconButton = composeTestRule.onNodeWithTag(TestTags.CLOSE_SEARCH_ICON_BUTTON)

        navDrawer = composeTestRule.onNodeWithTag(TestTags.NAV_DRAWER)
        rateAppTile = composeTestRule.onNodeWithTag(TestTags.RATE_APP_TILE)
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun ensureTopAppBarFunctionality() {
        // Initial state
        // Scenario : when app is launched, it will show title, menu icon, search icon
        topAppBar.assertIsDisplayed()
        searchIconButton.assertIsDisplayed()
        menuIconButton.assertIsDisplayed()
        topAppBarTitle.assertIsDisplayed()

        // When search button is pressed
        // Scenario : when pressed, a text field is shown
        searchIconButton.performClick()
        topAppBarTextField.assertIsDisplayed()

        /// 1. When textField is being edited
        /// Scenario : when pressed, it will gain focus then write text on it
        topAppBarTextField.performClick()
        topAppBarTextField.performTextInput("Input Text")
        composeTestRule.onNodeWithText("Input Text").assertExists()

        /// 2. When close button is pressed
        /// Scenario : when pressed, textfield is dismissed
        closeSearchIconButton.assertIsDisplayed()
        closeSearchIconButton.performClick()
        topAppBarTextField.assertDoesNotExist()
    }

    @Test
    fun ensureNavDrawerFunctionality() {
        // When menu button is pressed
        menuIconButton.performClick()
        navDrawer.assertIsDisplayed()

        // When drawer is displayed
        rateAppTile.assertIsDisplayed()
        rateAppTile.assertIsDisplayed()

        /// 1. When rate app is pressed
        ///
        rateAppTile.performClick()
        intended(hasAction(Intent.ACTION_VIEW))
        intended(hasData(Uri.parse("https://play.google.com/store/apps/details?id=com.digiventure.ventnote")))
    }
}