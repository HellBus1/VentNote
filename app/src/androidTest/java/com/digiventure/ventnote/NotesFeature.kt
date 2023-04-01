package com.digiventure.ventnote

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.digiventure.ventnote.commons.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NotesFeature {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var composeTestRule = createAndroidComposeRule(MainActivity::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    /**
     * Ensure top appbar and its children is showing
     * */
    @Test
    fun ensureDisplayTopAppBar() {
        // Initial state
        composeTestRule.onNodeWithTag(TestTags.TOP_APPBAR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.SEARCH_ICON_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.MENU_ICON_BUTTON).assertIsDisplayed()
    }

    @Test
    fun ensureDisplayNavDrawer() {

    }
}