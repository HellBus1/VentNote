package com.digiventure.ventnote

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.digiventure.utils.BaseAcceptanceTest
import com.digiventure.ventnote.data.NoteRepository
import com.digiventure.ventnote.data.local.NoteModel
import com.digiventure.ventnote.feature.notes.NotesPage
import com.digiventure.ventnote.feature.notes.viewmodel.NotesPageViewModel
import com.digiventure.ventnote.feature.notes.viewmodel.NotesPageViewModel_Factory
import com.digiventure.ventnote.ui.theme.VentNoteTheme
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

class NotesFeature: BaseAcceptanceTest() {
    @get:Rule(order = 0)
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

    @Test
    fun displayFAB() {
        composeTestRule.onNodeWithTag("add-note-fab").assertIsDisplayed()
    }
}