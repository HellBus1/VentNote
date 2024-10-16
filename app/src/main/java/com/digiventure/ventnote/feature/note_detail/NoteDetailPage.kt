package com.digiventure.ventnote.feature.note_detail

import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.ViewTreeObserver
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
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
import com.digiventure.ventnote.components.LockScreenOrientation
import com.digiventure.ventnote.components.dialog.LoadingDialog
import com.digiventure.ventnote.components.dialog.TextDialog
import com.digiventure.ventnote.feature.note_detail.components.NoteDetailAppBar
import com.digiventure.ventnote.feature.note_detail.viewmodel.NoteDetailPageBaseVM
import com.digiventure.ventnote.feature.note_detail.viewmodel.NoteDetailPageMockVM
import com.digiventure.ventnote.feature.note_detail.viewmodel.NoteDetailPageVM
import com.digiventure.ventnote.navigation.Route
import com.google.gson.Gson
import kotlinx.coroutines.launch

const val TAG : String = "NoteDetailPage"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailPage(
    navHostController: NavHostController,
    viewModel: NoteDetailPageBaseVM = hiltViewModel<NoteDetailPageVM>(),
    id: String
) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    // String resource
    val titleTextField = "${stringResource(R.string.title_textField)}-$TAG"
    val bodyTextField = "${stringResource(R.string.body_textField)}-$TAG"
    val titleInput = stringResource(R.string.title_textField_input)
    val bodyInput = stringResource(R.string.body_textField_input)

    val noteDetailState = viewModel.noteDetail.observeAsState()
    val data = noteDetailState.value?.getOrNull()

    val isEditingState = viewModel.isEditing.value
    val focusManager = LocalFocusManager.current

    val loadingState = viewModel.loader.observeAsState()

    val scope = rememberCoroutineScope()

    val requiredDialogState = remember { mutableStateOf(false) }
    val deleteDialogState = remember { mutableStateOf(false) }
    val cancelDialogState = remember { mutableStateOf(false) }
    val openLoadingDialog = remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }
    
    fun initData() {
        viewModel.titleText.value = data?.title ?: ""
        viewModel.descriptionText.value = data?.note ?: ""
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getNoteDetail(id.toInt())
    }

    LaunchedEffect(key1 = noteDetailState.value) {
        initData()
    }

    LaunchedEffect(key1 = isEditingState) {
        if (!isEditingState) {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(key1 = loadingState.value) {
        openLoadingDialog.value = loadingState.value == true
    }

    fun deleteNote() {
        if (data != null) {
            scope.launch {
                viewModel.deleteNoteList(data)
                    .onSuccess {
                        deleteDialogState.value = false
                        navHostController.popBackStack()
                    }
                    .onFailure {
                        deleteDialogState.value = false
                        snackBarHostState.showSnackbar(
                            message = it.message ?: "",
                            withDismissAction = true
                        )
                    }
            }
        }
    }

    fun updateNote() {
        if (viewModel.titleText.value.isEmpty() || viewModel.descriptionText.value.isEmpty()) {
            requiredDialogState.value = true
        } else {
            if (data != null) {
                focusManager.clearFocus()

                scope.launch {
                    val updatedNote = data.copy(title = viewModel.titleText.value, note = viewModel.descriptionText.value)
                    viewModel.updateNote(updatedNote)
                        .onSuccess {
                            viewModel.isEditing.value = false
                            snackBarHostState.showSnackbar(
                                message = "Note successfully updated",
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
    }

    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(appBarState)
    val rememberedScrollBehavior = remember { scrollBehavior }

    val view = LocalView.current
    val keyboardHeight = remember { mutableStateOf(0.dp) }

    val viewTreeObserver = remember { view.viewTreeObserver }
    val onGlobalLayoutListener = remember {
        ViewTreeObserver.OnGlobalLayoutListener {
            val rect = android.graphics.Rect().apply {
                view.getWindowVisibleDisplayFrame(this)
            }
            val keyboardHeightNew = view.rootView.height - rect.bottom
            if (keyboardHeightNew.dp != keyboardHeight.value) {
                keyboardHeight.value = keyboardHeightNew.dp
            }
        }
    }

    DisposableEffect(view) {
        viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
        onDispose {
            viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener)
        }
    }

    Scaffold(
        topBar = {
            NoteDetailAppBar(
                isEditing = isEditingState,
                descriptionTextLength = viewModel.descriptionText.value.length,
                onBackPressed = {
                    navHostController.popBackStack()
                },
                onClosePressed = {
                    cancelDialogState.value = true
                },
                onDeletePressed = {
                    deleteDialogState.value = true
                },
                scrollBehavior = rememberedScrollBehavior,
                onSharePressed = {
                    val json = Uri.encode(Gson().toJson(data))
                    navHostController.navigate("${Route.SharePreviewPage.routeName}/${json}")
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if(isEditingState) {
                        updateNote()
                    } else {
                        viewModel.isEditing.value = true
                    }
                },
                modifier = Modifier.semantics {
                    testTag = "edit-note-fab"
                },
                text = {
                    Text(if(isEditingState) stringResource(R.string.save) else stringResource(R.string.edit),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium)
                },
                icon = {
                    Icon(
                        imageVector = if(isEditingState) Icons.Filled.Check else Icons.Filled.Edit,
                        contentDescription = stringResource(R.string.fab),
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
                        .padding(bottom = keyboardHeight.value)
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
                        readOnly = !isEditingState,
                        enabled = isEditingState,
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
                        readOnly = !isEditingState,
                        enabled = isEditingState,
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
                            .fillMaxHeight()
                            .semantics { contentDescription = bodyTextField },
                        placeholder = {
                            Text(
                                text = bodyInput,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                    )
                }
            }
        },
        modifier = Modifier
            .semantics { testTag = TestTags.NOTE_DETAIL_PAGE }
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
        onConfirmCallback = { requiredDialogState.value = false }
    )

    TextDialog(
        title = stringResource(R.string.cancel_title),
        description = stringResource(R.string.cancel_confirmation_text),
        isOpened = cancelDialogState.value,
        onDismissCallback = { cancelDialogState.value = false },
        onConfirmCallback = {
            viewModel.isEditing.value = false
            cancelDialogState.value = false

            initData()
        })

    TextDialog(
        isOpened = deleteDialogState.value,
        onDismissCallback = { deleteDialogState.value = false },
        onConfirmCallback = { deleteNote() })

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