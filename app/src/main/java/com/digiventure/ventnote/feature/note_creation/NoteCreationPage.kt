package com.digiventure.ventnote.feature.note_creation

import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.components.LockScreenOrientation
import com.digiventure.ventnote.components.dialog.TextDialog
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.feature.note_creation.components.NoteCreationAppBar
import com.digiventure.ventnote.feature.note_creation.viewmodel.NoteCreationPageBaseVM
import com.digiventure.ventnote.feature.note_creation.viewmodel.NoteCreationPageMockVM
import com.digiventure.ventnote.feature.note_creation.viewmodel.NoteCreationPageVM
import kotlinx.coroutines.launch

const val TAG : String = "NoteCreationPage"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCreationPage(
    navHostController: NavHostController,
    viewModel: NoteCreationPageBaseVM = hiltViewModel<NoteCreationPageVM>()
) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    // String resource
    val titleTextField = "${stringResource(R.string.title_textField)}-${TAG}"
    val bodyTextField = "${stringResource(R.string.body_textField)}-${TAG}"
    val titleInput = stringResource(R.string.title_textField_input)
    val bodyInput = stringResource(R.string.body_textField_input)

    val length = viewModel.descriptionText.value.length

    val scope = rememberCoroutineScope()

    val requiredDialogState = remember { mutableStateOf(false) }
    val cancelDialogState = remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }

    fun addNote() {
        if (viewModel.titleText.value.isEmpty() || viewModel.descriptionText.value.isEmpty()) {
            requiredDialogState.value = true
        } else {
            scope.launch {
                viewModel.addNote(
                    NoteModel(
                    title = viewModel.titleText.value,
                    note = viewModel.descriptionText.value)
                )
                    .onSuccess {
                        navHostController.popBackStack()
                        snackBarHostState.showSnackbar(
                            message = "Note successfully added",
                            withDismissAction = true
                        )
                    }
                    .onFailure {
                        snackBarHostState.showSnackbar(
                            message = it.message ?: "",
                            withDismissAction = true
                        )
                    }
            }
        }
    }

    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(appBarState)
    val rememberedScrollBehavior = remember { scrollBehavior }

    Scaffold(
        topBar = {
            NoteCreationAppBar(
                descriptionTextLength = length,
                onBackPressed = {
                    cancelDialogState.value = true
                },
                scrollBehavior = rememberedScrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { addNote() },
                modifier = Modifier.semantics {
                    testTag = "add-note-fab"
                },
                text = {
                    Text(stringResource(R.string.save), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(R.string.fab)
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        },
        content = { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    TextField(
                        value = viewModel.titleText.value,
                        onValueChange = {
                            viewModel.titleText.value = it
                        },
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = false,
                        colors = TextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = titleTextField },
                        placeholder = {
                            Text(
                                text = titleInput,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                    TextField(
                        value = viewModel.descriptionText.value,
                        onValueChange = {
                            viewModel.descriptionText.value = it
                        },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = false,
                        shape = RectangleShape,
                        colors = TextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize()
                            .semantics { contentDescription = bodyTextField },
                        placeholder = {
                            Text(
                                text = bodyInput,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }
        },
        modifier = Modifier
            .semantics { testTag = TestTags.NOTE_CREATION_PAGE }
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface
    )

    val missingFieldName = if (viewModel.titleText.value.isEmpty()) {
        "Title"
    } else if (viewModel.descriptionText.value.isEmpty()) {
        "Notes"
    } else {
        ""
    }

    TextDialog(
        title = stringResource(R.string.required_title),
        description = stringResource(R.string.required_confirmation_text, missingFieldName),
        isOpened = requiredDialogState.value,
        onDismissCallback = { requiredDialogState.value = false },
        onConfirmCallback = { requiredDialogState.value = false })

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

@Preview
@Composable
fun NoteCreationPagePreview() {
    NoteCreationPage(
        navHostController = rememberNavController(),
        viewModel = NoteCreationPageMockVM()
    )
}