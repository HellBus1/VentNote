package com.digiventure.ventnote.feature.note_creation

import android.content.pm.ActivityInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.Constants.EMPTY_STRING
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.components.LockScreenOrientation
import com.digiventure.ventnote.components.dialog.TextDialog
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.feature.note_creation.components.navbar.EnhancedBottomAppBar
import com.digiventure.ventnote.feature.note_creation.components.navbar.NoteCreationAppBar
import com.digiventure.ventnote.feature.note_creation.components.section.NoteSection
import com.digiventure.ventnote.feature.note_creation.components.section.TitleSection
import com.digiventure.ventnote.feature.note_creation.viewmodel.NoteCreationPageBaseVM
import com.digiventure.ventnote.feature.note_creation.viewmodel.NoteCreationPageMockVM
import com.digiventure.ventnote.feature.note_creation.viewmodel.NoteCreationPageVM
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCreationPage(
    navHostController: NavHostController,
    viewModel: NoteCreationPageBaseVM = hiltViewModel<NoteCreationPageVM>()
) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    val titleTextFieldText = stringResource(R.string.title_textField)
    val bodyTextFieldText = stringResource(R.string.body_textField)

    // String resources - optimized to avoid recomputation
    val titleTextField = remember { "${titleTextFieldText}-${TestTags.NOTE_CREATION_PAGE}" }
    val bodyTextField = remember { "${bodyTextFieldText}-${TestTags.NOTE_CREATION_PAGE}" }
    val titleInput = stringResource(R.string.title_textField_input)
    val bodyInput = stringResource(R.string.body_textField_input)

    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    // Optimized state management - using delegation for better performance
    val requiredDialogState = remember { mutableStateOf(false) }
    val cancelDialogState = remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }

    // Extracted and optimized addNote function
    val noteIsSuccessfullyAddedText = stringResource(R.string.successfully_added)
    val addNote = remember {
        {
            if (viewModel.titleText.value.isEmpty() || viewModel.descriptionText.value.isEmpty()) {
                requiredDialogState.value = true
            } else {
                scope.launch {
                    viewModel.addNote(
                        NoteModel(
                            id = 0,
                            title = viewModel.titleText.value,
                            note = viewModel.descriptionText.value
                        )
                    ).onSuccess {
                        navHostController.popBackStack()
                        snackBarHostState.showSnackbar(
                            message = noteIsSuccessfullyAddedText,
                            withDismissAction = true
                        )
                    }.onFailure {
                        snackBarHostState.showSnackbar(
                            message = it.message ?: EMPTY_STRING,
                            withDismissAction = true
                        )
                    }
                }
            }
        }
    }

    // Optimized scroll behavior - remember to prevent recreation
    val appBarState = rememberTopAppBarState()
    val scrollBehaviorState = TopAppBarDefaults.enterAlwaysScrollBehavior(appBarState)
    val scrollBehavior = remember { scrollBehaviorState }

    Scaffold(
        topBar = {
            NoteCreationAppBar(
                onBackPressed = {
                    cancelDialogState.value = true
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        },
        content = { contentPadding ->
            // Better scrolling performance with LazyColumn
            LazyColumn(
                modifier = Modifier
                    .padding(contentPadding)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        focusManager.clearFocus()
                    }
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    TitleSection(viewModel, titleTextField, titleInput)
                }
                item {
                    NoteSection(viewModel, bodyTextField, bodyInput)
                }
            }
        },
        bottomBar = {
            EnhancedBottomAppBar {
                addNote()
            }
        },
        modifier = Modifier
            .semantics { testTag = TestTags.NOTE_CREATION_PAGE }
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface
    )

    // Optimized missing field calculation
    val emptyTitleText = stringResource(R.string.empty_note_title_placeholder)
    val emptyNoteText = stringResource(R.string.empty_note_placeholder)
    val missingFieldName = remember(viewModel.titleText.value, viewModel.descriptionText.value) {
        when {
            viewModel.titleText.value.isEmpty() -> emptyTitleText
            viewModel.descriptionText.value.isEmpty() -> emptyNoteText
            else -> EMPTY_STRING
        }
    }

    if (requiredDialogState.value) {
        TextDialog(
            title = stringResource(R.string.required_title),
            description = stringResource(R.string.required_confirmation_text, missingFieldName),
            isOpened = requiredDialogState.value,
            onDismissCallback = { requiredDialogState.value = false },
            onConfirmCallback = { requiredDialogState.value = false })
    }

    if (cancelDialogState.value) {
        TextDialog(
            title = stringResource(R.string.cancel_title),
            description = stringResource(R.string.cancel_confirmation_text),
            isOpened = cancelDialogState.value,
            onDismissCallback = { cancelDialogState.value = false },
            onConfirmCallback = {
                navHostController.popBackStack()
                cancelDialogState.value = false
            })
    }
}

@Preview
@Composable
fun NoteCreationPagePreview() {
    NoteCreationPage(
        navHostController = rememberNavController(),
        viewModel = NoteCreationPageMockVM()
    )
}