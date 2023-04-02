package com.digiventure.ventnote.feature.noteCreation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.components.dialog.TextDialog
import com.digiventure.ventnote.data.local.NoteModel
import com.digiventure.ventnote.feature.noteCreation.components.NoteCreationAppBar
import com.digiventure.ventnote.feature.noteCreation.viewmodel.NoteCreationPageBaseVM
import com.digiventure.ventnote.feature.noteCreation.viewmodel.NoteCreationPageMockVM
import com.digiventure.ventnote.feature.noteCreation.viewmodel.NoteCreationPageVM
import com.digiventure.ventnote.ui.theme.PurpleGrey80
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCreationPage(
    navHostController: NavHostController,
    viewModel: NoteCreationPageBaseVM = hiltViewModel<NoteCreationPageVM>()
) {
    val length = viewModel.descriptionText.value.length

    val requiredDialogState = remember { mutableStateOf(false) }
    val cancelDialogState = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    fun addNote() {
        if (viewModel.titleText.value.isEmpty() || viewModel.descriptionText.value.isEmpty()) {
            requiredDialogState.value = true
        } else {
            scope.launch {
                viewModel.addNote(NoteModel(
                    title = viewModel.titleText.value,
                    note = viewModel.descriptionText.value))
                    .onSuccess {
                        navHostController.popBackStack()
                        snackbarHostState.showSnackbar(
                            message = "Note successfully added",
                            withDismissAction = true
                        )
                    }
                    .onFailure {
                        snackbarHostState.showSnackbar(
                            message = it.message ?: "",
                            withDismissAction = true
                        )
                    }
            }
        }
    }

    Scaffold(
        topBar = {
            NoteCreationAppBar(descriptionTextLength = length, onBackPressed = {
                cancelDialogState.value = true
            })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { addNote() },
                modifier = Modifier.semantics {
                    testTag = "add-note-fab"
                }) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(R.string.fab),)
            }
        },
        content = { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                Column {
                    Box() {
                        OutlinedTextField(
                            value = viewModel.titleText.value,
                            onValueChange = {
                                viewModel.titleText.value = it
                            },
                            textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                            singleLine = true,
                            modifier = Modifier
                                .border(
                                    width = 3.dp,
                                    color = PurpleGrey80,
                                    shape = RectangleShape
                                )
                                .fillMaxWidth(),
                            placeholder = { Text("Insert title", fontSize = 18.sp, color = PurpleGrey80) }
                        )
                    }
                    Box() {
                        TextField(
                            value = viewModel.descriptionText.value,
                            onValueChange = {
                                viewModel.descriptionText.value = it
                            },
                            textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal),
                            singleLine = false,
                            shape = RectangleShape,
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.Transparent,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            placeholder = { Text("Insert note", fontSize = 18.sp, color = PurpleGrey80) }
                        )
                    }
                }
            }
        },
        modifier = Modifier.semantics { testTag = TestTags.NOTE_CREATION_PAGE }
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