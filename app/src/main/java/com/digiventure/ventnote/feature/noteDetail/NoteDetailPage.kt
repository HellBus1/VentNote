package com.digiventure.ventnote.feature.noteDetail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.digiventure.ventnote.components.dialog.LoadingDialog
import com.digiventure.ventnote.components.dialog.TextDialog
import com.digiventure.ventnote.feature.noteDetail.components.NoteDetailAppBar
import com.digiventure.ventnote.feature.noteDetail.viewmodel.NoteDetailPageBaseVM
import com.digiventure.ventnote.feature.noteDetail.viewmodel.NoteDetailPageMockVM
import com.digiventure.ventnote.feature.noteDetail.viewmodel.NoteDetailPageVM
import com.digiventure.ventnote.ui.theme.PurpleGrey80
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailPage(
    navHostController: NavHostController,
    viewModel: NoteDetailPageBaseVM = hiltViewModel<NoteDetailPageVM>(),
    id: String
) {
    val noteDetailState = viewModel.noteDetail.observeAsState()
    LaunchedEffect(key1 = Unit) {
        viewModel.getNoteDetail(id.toInt())
    }

    LaunchedEffect(key1 = noteDetailState.value) {
        val data = noteDetailState.value?.getOrNull()
        viewModel.titleText.value = data?.title ?: ""
        viewModel.descriptionText.value = data?.note ?: ""
    }

    val isEditingState = viewModel.isEditing.value

    val requiredDialogState = remember { mutableStateOf(false) }
    val deleteDialogState = remember { mutableStateOf(false) }
    val cancelDialogState = remember { mutableStateOf(false) }
    val openLoadingDialog = remember { mutableStateOf(false) }

    val loadingState = viewModel.loader.observeAsState()
    LaunchedEffect(key1 = loadingState.value) {
        openLoadingDialog.value = loadingState.value == true
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            NoteDetailAppBar(
                isEditing = isEditingState,
                descriptionTextLength = viewModel.descriptionText.value.length,
                onBackPressed = {
                    navHostController.popBackStack()
                }, onClosePressed = {
                    cancelDialogState.value = true
                }, onDeletePressed = {
                    deleteDialogState.value = true
                })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.isEditing.value = true },
                modifier = Modifier.semantics {
                    testTag = "edit-note-fab"
                }) {
                Icon(
                    imageVector = if(isEditingState) Icons.Filled.Check else Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.fab),)
            }
        }
    ) { contentPadding ->
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
                        readOnly = !isEditingState,
                        modifier = Modifier
                            .border(
                                width = 3.dp,
                                color = PurpleGrey80,
                                shape = RectangleShape
                            )
                            .fillMaxWidth(),
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
                        readOnly = !isEditingState,
                        shape = RectangleShape,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                    )
                }
            }
        }
    }

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
            viewModel.isEditing.value = false
            cancelDialogState.value = false
        })

    TextDialog(
        isOpened = deleteDialogState.value,
        onDismissCallback = { deleteDialogState.value = false },
        onConfirmCallback = {
            val value = noteDetailState.value?.getOrNull()
            if (value != null) {
                scope.launch {
                    val result = viewModel.deleteNoteList(value)
                    deleteDialogState.value = false

                    if (result.isSuccess) {
                        navHostController.popBackStack()
                    } else {
                        result.getOrElse { error ->
                            snackbarHostState.showSnackbar(
                                message = error.message ?: "",
                                withDismissAction = true
                            )

                            deleteDialogState.value = false
                        }
                    }
                }
            }
        })

    LoadingDialog(isOpened = openLoadingDialog.value, onDismissCallback = { openLoadingDialog.value = false })

    BackHandler {
        if (viewModel.isEditing.value) {
            cancelDialogState.value = true
        } else {
            navHostController.popBackStack()
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