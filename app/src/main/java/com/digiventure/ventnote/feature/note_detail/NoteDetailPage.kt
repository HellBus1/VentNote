package com.digiventure.ventnote.feature.note_detail

import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
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
import com.digiventure.ventnote.components.dialog.LoadingDialog
import com.digiventure.ventnote.components.dialog.TextDialog
import com.digiventure.ventnote.feature.note_detail.components.navbar.EnhancedBottomAppBar
import com.digiventure.ventnote.feature.note_detail.components.navbar.NoteDetailAppBar
import com.digiventure.ventnote.feature.note_detail.components.section.NoteSection
import com.digiventure.ventnote.feature.note_detail.components.section.TitleSection
import com.digiventure.ventnote.feature.note_detail.viewmodel.NoteDetailPageBaseVM
import com.digiventure.ventnote.feature.note_detail.viewmodel.NoteDetailPageMockVM
import com.digiventure.ventnote.feature.note_detail.viewmodel.NoteDetailPageVM
import com.digiventure.ventnote.navigation.PageNavigation
import com.google.gson.Gson
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailPage(
    navHostController: NavHostController,
    viewModel: NoteDetailPageBaseVM = hiltViewModel<NoteDetailPageVM>(),
    id: String
) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    val navigationActions = remember(navHostController) {
        PageNavigation(navHostController)
    }

    // String resources - memoized for better performance
    val titleTextFieldContentDescription = stringResource(R.string.title_textField)
    val bodyTextFieldContentDescription = stringResource(R.string.body_textField)
    val titleInputPlaceholder = stringResource(R.string.title_textField_input)
    val bodyInputPlaceholder = stringResource(R.string.body_textField_input)
    val successFullyUpdatedLabel = stringResource(R.string.successfully_updated)

    val strings = remember {
        mapOf(
            "titleTextField" to titleTextFieldContentDescription,
            "bodyTextField" to bodyTextFieldContentDescription,
            "titleInput" to titleInputPlaceholder,
            "bodyInput" to bodyInputPlaceholder,
            "successFullyUpdatedText" to successFullyUpdatedLabel
        )
    }

    // State observers
    val noteDetailState by viewModel.noteDetail.observeAsState()
    val data = noteDetailState?.getOrNull()
    val isEditingState by viewModel.isEditing
    val loadingState by viewModel.loader.observeAsState()

    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    // Dialog states - using derivedStateOf where appropriate
    val requiredDialogState = remember { mutableStateOf(false) }
    val deleteDialogState = remember { mutableStateOf(false) }
    val cancelDialogState = remember { mutableStateOf(false) }
    val openLoadingDialog = remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }

    // Memoized functions to prevent unnecessary recompositions
    val initData = {
        data?.let {
            viewModel.titleText.value = it.title
            viewModel.descriptionText.value = it.note
        }
    }

    val deleteNote = remember(data, scope, snackBarHostState) {
        {
            data?.let { noteData ->
                scope.launch {
                    viewModel.deleteNoteList(noteData)
                        .onSuccess {
                            deleteDialogState.value = false
                            navHostController.navigateUp()
                        }
                        .onFailure { error ->
                            deleteDialogState.value = false
                            snackBarHostState.showSnackbar(
                                message = error.message ?: "",
                                withDismissAction = true
                            )
                        }
                }
            }
        }
    }

    val updateNote = remember(
        data,
        scope,
        focusManager,
        snackBarHostState,
        strings["successFullyUpdatedText"]
    ) {
        {
            val titleText = viewModel.titleText.value
            val descriptionText = viewModel.descriptionText.value

            if (titleText.isEmpty() || descriptionText.isEmpty()) {
                requiredDialogState.value = true
            } else {
                data?.let { noteData ->
                    focusManager.clearFocus()

                    scope.launch {
                        val updatedNote = noteData.copy(
                            title = titleText,
                            note = descriptionText
                        )
                        viewModel.updateNote(updatedNote)
                            .onSuccess {
                                viewModel.isEditing.value = false
                                snackBarHostState.showSnackbar(
                                    message = strings["successFullyUpdatedText"] ?: "",
                                    withDismissAction = true
                                )
                            }
                            .onFailure { error ->
                                snackBarHostState.showSnackbar(
                                    message = error.message ?: "",
                                    withDismissAction = true
                                )
                            }
                    }
                }
            }
        }
    }

    // Effects
    LaunchedEffect(id) {
        viewModel.getNoteDetail(id.toInt())
    }

    LaunchedEffect(noteDetailState) {
        initData()
    }

    LaunchedEffect(isEditingState) {
        if (!isEditingState) {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(loadingState) {
        openLoadingDialog.value = loadingState == true
    }

    // Scroll behavior setup
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

    val haptics = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            NoteDetailAppBar(
                isEditing = isEditingState,
                onBackPressed = {
                    navigationActions.navigateToNotesPage()
                },
                scrollBehavior = scrollBehavior,
                onSharePressed = {
                    data?.let {
                        val json = Uri.encode(Gson().toJson(it))
                        navigationActions.navigateToSharePage(json)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        content = { contentPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 16.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        focusManager.clearFocus()
                    },
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    TitleSection(
                        viewModel = viewModel,
                        isEditingState = isEditingState,
                        titleTextField = strings["titleTextField"] ?: EMPTY_STRING,
                        titleInput = strings["titleInput"] ?: EMPTY_STRING
                    )
                }

                item {
                    NoteSection(
                        viewModel = viewModel,
                        isEditingState = isEditingState,
                        bodyTextField = strings["bodyTextField"] ?: EMPTY_STRING,
                        bodyInput = strings["bodyInput"] ?: EMPTY_STRING
                    )
                }
            }
        },
        bottomBar = {
            EnhancedBottomAppBar(
                isEditing = isEditingState,
                onEditClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.isEditing.value = true
                },
                onSaveClick = { updateNote() },
                onDeleteClick = { deleteDialogState.value = true },
                onCancelClick = { cancelDialogState.value = true }
            )
        },
        modifier = Modifier
            .semantics { testTag = TestTags.NOTE_DETAIL_PAGE }
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
    )

    // Dialogs
    val titlePlaceholderText = stringResource(R.string.title_textField_input)
    val notePlaceholderText = stringResource(R.string.body_textField_input)

    val missingFieldName = remember(
        viewModel.titleText.value,
        viewModel.descriptionText.value
    ) {
        when {
            viewModel.titleText.value.isEmpty() -> titlePlaceholderText
            viewModel.descriptionText.value.isEmpty() -> notePlaceholderText
            else -> EMPTY_STRING
        }
    }

    if (requiredDialogState.value) {
        TextDialog(
            title = stringResource(R.string.required_title),
            description = stringResource(R.string.required_confirmation_text, missingFieldName),
            isOpened = requiredDialogState.value,
            onDismissCallback = { requiredDialogState.value = false },
            onConfirmCallback = { requiredDialogState.value = false }
        )
    }

    if (cancelDialogState.value) {
        TextDialog(
            title = stringResource(R.string.cancel_title),
            description = stringResource(R.string.cancel_confirmation_text),
            isOpened = cancelDialogState.value,
            onDismissCallback = { cancelDialogState.value = false },
            onConfirmCallback = {
                viewModel.isEditing.value = false
                cancelDialogState.value = false
                initData()
            }
        )
    }

    if (deleteDialogState.value) {
        TextDialog(
            isOpened = deleteDialogState.value,
            onDismissCallback = { deleteDialogState.value = false },
            onConfirmCallback = { deleteNote() }
        )
    }

    if (openLoadingDialog.value) {
        LoadingDialog(
            isOpened = openLoadingDialog.value,
            onDismissCallback = { openLoadingDialog.value = false }
        )
    }

    BackHandler {
        if (viewModel.isEditing.value) {
            cancelDialogState.value = true
        } else {
            navHostController.navigateUp()
        }
    }
}

@Preview
@Composable
fun NoteDetailPagePreview() {
    NoteDetailPage(
        navHostController = rememberNavController(),
        viewModel = NoteDetailPageMockVM(),
        id = "0"
    )
}