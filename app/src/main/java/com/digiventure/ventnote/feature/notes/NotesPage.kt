package com.digiventure.ventnote.feature.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.DateUtil
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.components.dialog.LoadingDialog
import com.digiventure.ventnote.components.dialog.TextDialog
import com.digiventure.ventnote.data.local.NoteModel
import com.digiventure.ventnote.feature.notes.components.NavDrawer
import com.digiventure.ventnote.feature.notes.components.NotesAppBar
import com.digiventure.ventnote.feature.notes.viewmodel.NotesPageBaseVM
import com.digiventure.ventnote.feature.notes.viewmodel.NotesPageMockVM
import com.digiventure.ventnote.feature.notes.viewmodel.NotesPageVM
import com.digiventure.ventnote.navigation.Route
import kotlinx.coroutines.launch

@Composable
fun NotesPage(
    navHostController: NavHostController,
    viewModel: NotesPageBaseVM = hiltViewModel<NotesPageVM>()
) {
    val noteListState = viewModel.noteList.observeAsState()
    val loadingState = viewModel.loader.observeAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }
    val filteredNoteListState = remember { mutableStateOf<List<NoteModel>>(listOf()) }
    val loadingDialog = remember { mutableStateOf(false) }
    val deleteDialog = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = noteListState.value) {
        // Showing error snackBar on error
        noteListState.value?.onFailure {
            scope.launch {
                snackBarHostState.showSnackbar(
                    message = it.message ?: "",
                    withDismissAction = true
                )
            }
        }
    }

    LaunchedEffect(noteListState.value, viewModel.searchedTitleText.value) {
        // Replace filteredNoteListState value with filtered noteList state
        // every searchedTitleText changed
        filteredNoteListState.value = noteListState.value?.getOrNull()?.filter { note ->
            note.title.contains(viewModel.searchedTitleText.value, true)
        } ?: listOf()
    }

    LaunchedEffect(key1 = loadingState.value) {
        // Showing loading dialog whenever loading state is true
        loadingDialog.value = (loadingState.value == true)
    }

    val deletedMessage = stringResource(id = R.string.note_is_successfully_deleted)

    fun deleteNoteList() {
        scope.launch {
            viewModel.deleteNoteList()
                .onSuccess {
                    deleteDialog.value = false
                    viewModel.unMarkAllNote()

                    snackBarHostState.showSnackbar(
                        message = deletedMessage,
                        withDismissAction = true
                    )
                }
                .onFailure {
                    deleteDialog.value = false

                    snackBarHostState.showSnackbar(
                        message = it.message ?: "",
                        withDismissAction = true
                    )
                }
        }
    }

    NavDrawer(
        navHostController = navHostController,
        drawerState = drawerState,
        onError = {
            scope.launch {
                snackBarHostState.showSnackbar(
                    message = it,
                    withDismissAction = true
                )
            }
        },
        content = {
            Scaffold(
                topBar = {
                    NotesAppBar(
                        isMarking = viewModel.isMarking.value,
                        markedNoteListSize = viewModel.markedNoteList.size,
                        isSearching = viewModel.isSearching.value,
                        searchedTitle = viewModel.searchedTitleText.value,
                        toggleDrawerCallback = {
                            scope.launch {
                                drawerState.open()
                            }
                        },
                        selectAllCallback = {
                            viewModel.noteList.value?.getOrNull().let {
                                if (it != null) viewModel.markAllNote(it)
                            }
                        },
                        unSelectAllCallback = {
                            viewModel.unMarkAllNote()
                        },
                        onSearchValueChange = {
                            viewModel.searchedTitleText.value = it
                        },
                        closeMarkingCallback = {
                            // Close marking state and clear marked notes
                            viewModel.isMarking.value = false
                            viewModel.markedNoteList.clear()
                        },
                        searchCallback = {
                            viewModel.isSearching.value = !viewModel.isSearching.value
                            viewModel.searchedTitleText.value = ""
                        },
                        deleteCallback = {
                            deleteDialog.value = true
                        }
                    )
                },
                snackbarHost = { SnackbarHost(snackBarHostState) },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        onClick = {
                            viewModel.isMarking.value = false
                            viewModel.markedNoteList.clear()
                            navHostController.navigate(Route.NoteCreationPage.routeName)
                        },
                        modifier = Modifier.semantics {
                            testTag = TestTags.ADD_NOTE_FAB
                        },
                        text = {
                            Text(stringResource(R.string.add), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = stringResource(R.string.fab)
                            )
                        }
                    )
                },
                content = { contentPadding ->
                    Box(modifier = Modifier.padding(contentPadding)) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .semantics { testTag = TestTags.NOTE_RV },
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(
                                top = 24.dp,
                                bottom = 96.dp
                            )
                        ) {
                            items(items = filteredNoteListState.value) {
                                NotesItem(
                                    isMarking = viewModel.isMarking.value,
                                    isMarked = it in viewModel.markedNoteList,
                                    data = it,
                                    onClick = {
                                        if (viewModel.isMarking.value) {
                                            viewModel.addToMarkedNoteList(it)
                                        } else {
                                            navHostController.navigate("${Route.NoteDetailPage.routeName}/${it.id}")
                                        }
                                    },
                                    onLongClick = {
                                        if (!viewModel.isMarking.value) {
                                            viewModel.isMarking.value = true
                                        }
                                        viewModel.addToMarkedNoteList(it)
                                    },
                                    onCheckClick = {
                                        viewModel.addToMarkedNoteList(it)
                                    }
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.semantics { testTag = TestTags.NOTES_PAGE }
            )
        }
    )

    LoadingDialog(isOpened = loadingDialog.value, onDismissCallback = { loadingDialog.value = false },
        modifier = Modifier.semantics { testTag = TestTags.LOADING_DIALOG })

    TextDialog(isOpened = deleteDialog.value,
        onDismissCallback = { deleteDialog.value = false },
        onConfirmCallback = { deleteNoteList() },
        modifier = Modifier.semantics { testTag = TestTags.CONFIRMATION_DIALOG })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesItem(
    isMarking: Boolean,
    isMarked: Boolean,
    data: NoteModel,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onCheckClick: () -> Unit)
{
    Box(modifier = Modifier
        .padding(horizontal = 16.dp)
        .semantics { contentDescription = "Note item ${data.id}" }) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            )
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isMarking) {
                    Checkbox(checked = isMarked, onCheckedChange = { onCheckClick() },
                        modifier = Modifier.semantics { testTag = data.title })
                }
                
                Column(modifier = Modifier.weight(2f)) {
                    ItemText(text = data.title)
                }
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                    ItemText(text = DateUtil.convertDateString("EEE, MMM d", data.createdAt.toString()))
                    ItemText(text = DateUtil.convertDateString("h:mm a", data.createdAt.toString()))
                }
            }
        }
    }
}

@Composable
fun ItemText(text: String, color: Color = Color.Black) {
    Text(
        text = text,
        fontSize = 16.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = color
    )
}

@Preview
@Composable
fun NotesPagePreview() {
    NotesPage(
        navHostController = rememberNavController(),
        viewModel = NotesPageMockVM()
    )
}