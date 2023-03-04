package com.digiventure.ventnote

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.digiventure.utils.BaseAcceptanceTest
import com.digiventure.ventnote.feature.notes.NotesPage
import com.digiventure.ventnote.ui.theme.VentNoteTheme
import org.junit.Before
import org.junit.Test

class NotesFeature: BaseAcceptanceTest() {
    private lateinit var navHostController: NavHostController

    @Before
    fun setUp() {
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

    @Test
    fun displayFAB() {
        composeTestRule.onNodeWithTag("add-note").assertIsDisplayed()
    }
}