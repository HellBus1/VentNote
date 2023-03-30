package com.digiventure.ventnote

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.digiventure.utils.BaseAcceptanceTest
import com.digiventure.ventnote.feature.notes.NotesPage
import com.digiventure.ventnote.ui.theme.VentNoteTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NotesFeature {
    @get:Rule(order = 0)
    var composeTestRule = createComposeRule()

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    private lateinit var navHostController: NavHostController

    @Before
    fun setUp() {
        hiltRule.inject()

        composeTestRule.setContent {
            navHostController = rememberNavController()

            VentNoteTheme {
                NotesPage(navHostController)
            }
        }
    }

    @Test
    fun displayTopAppBar() {
        composeTestRule.onNodeWithTag("top-appBar").assertIsDisplayed()
    }
}