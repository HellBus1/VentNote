package com.digiventure.ventnote.feature.notes

import android.content.pm.ActivityInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
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
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.feature.notes.components.item.NotesItem
import com.digiventure.ventnote.feature.notes.components.navbar.NotesAppBar
import com.digiventure.ventnote.feature.notes.components.searchbar.SearchBar
import com.digiventure.ventnote.feature.notes.components.sheets.FilterSheet
import com.digiventure.ventnote.feature.notes.viewmodel.NotesPageBaseVM
import com.digiventure.ventnote.feature.notes.viewmodel.NotesPageMockVM
import com.digiventure.ventnote.feature.notes.viewmodel.NotesPageVM
import com.digiventure.ventnote.navigation.PageNavigation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesPage(
    navHostController: NavHostController,
    viewModel: NotesPageBaseVM = hiltViewModel<NotesPageVM>(),
    openDrawer: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var searchBarHeightPx by remember { mutableFloatStateOf(0f) }

    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    val navigationActions = remember(navHostController) {
        PageNavigation(navHostController)
    }

    // Observe states
    val noteListState by viewModel.noteList.observeAsState()
    val loadingState by viewModel.loader.observeAsState()
    val searchQuery by viewModel.searchedTitleText
    val isMarking by viewModel.isMarking
    val markedNoteList = viewModel.markedNoteList

    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    // Memoized filtered notes with proper dependencies
    val filteredNotes by remember {
        derivedStateOf {
            val notes = noteListState?.getOrNull() ?: emptyList()
            if (searchQuery.isBlank()) {
                notes
            } else {
                notes.filter { note ->
                    note.title.contains(searchQuery, ignoreCase = true) ||
                            note.note.contains(searchQuery, ignoreCase = true)
                }
            }
        }
    }

    // Dialog states
    var showLoadingDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Bottom sheet states
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    // Effects
    LaunchedEffect(Unit) {
        viewModel.observeNotes()
    }

    LaunchedEffect(noteListState) {
        noteListState?.onFailure { error ->
            snackBarHostState.showSnackbar(
                message = error.message.orEmpty(),
                withDismissAction = true
            )
        }
    }

    LaunchedEffect(loadingState) {
        showLoadingDialog = loadingState == true
    }

    // Memoized callbacks
    val deleteNoteList = remember {
        {
            scope.launch {
                viewModel.deleteNoteList()
                    .onSuccess {
                        showDeleteDialog = false
                        viewModel.unMarkAllNote()
                        viewModel.closeMarkingEvent()

                        snackBarHostState.showSnackbar(
                            message = "Note is successfully deleted", // Consider using stringResource
                            withDismissAction = true
                        )
                    }
                    .onFailure { error ->
                        showDeleteDialog = false
                        snackBarHostState.showSnackbar(
                            message = error.message.orEmpty(),
                            withDismissAction = true
                        )
                    }
            }
        }
    }

    val onNoteClick = remember {
        { note: NoteModel ->
            if (isMarking) {
                viewModel.addToMarkedNoteList(note)
            } else {
                viewModel.closeMarkingEvent()
                navigationActions.navigateToDetailPage(note.id)
            }
        }
    }

    val onNoteLongClick = remember {
        { note: NoteModel ->
            if (!isMarking) {
                viewModel.isMarking.value = true
            }
            viewModel.addToMarkedNoteList(note)
        }
    }

    val onNoteCheckClick = remember {
        { note: NoteModel ->
            viewModel.addToMarkedNoteList(note)
        }
    }

    Scaffold(
        topBar = {
            NotesAppBar(
                isMarking = isMarking,
                markedNoteListSize = markedNoteList.size,
                toggleDrawerCallback = openDrawer,
                selectAllCallback = {
                    noteListState?.getOrNull()?.let { notes ->
                        viewModel.markAllNote(notes)
                    }
                },
                unSelectAllCallback = viewModel::unMarkAllNote,
                closeMarkingCallback = viewModel::closeMarkingEvent,
                sortCallback = { openBottomSheet = true },
                deleteCallback = { showDeleteDialog = true }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.closeMarkingEvent()
                    navigationActions.navigateToCreatePage()
                },
                modifier = Modifier.semantics {
                    testTag = TestTags.ADD_NOTE_FAB
                },
                text = {
                    Text(
                        text = stringResource(R.string.add),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.fab)
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        },
        content = { contentPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        focusManager.clearFocus()
                    }
                    .padding(contentPadding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .semantics { testTag = TestTags.NOTE_RV },
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    // SearchBar as first item
                    item(key = "search_bar") {
                        Box(
                            modifier = Modifier
                                .onGloballyPositioned { coords ->
                                    searchBarHeightPx = coords.size.height.toFloat()
                                    scrollBehavior.state.heightOffsetLimit = -searchBarHeightPx
                                }
                                .graphicsLayer {
                                    translationY = scrollBehavior.state.heightOffset
                                }
                                .fillMaxWidth()
                                .padding(16.dp, 24.dp, 16.dp, 8.dp)
                        ) {
                            SearchBar(
                                query = searchQuery,
                                onQueryChange = { newQuery ->
                                    viewModel.searchedTitleText.value = newQuery
                                }
                            )
                        }
                    }

                    // Notes items with keys for better performance
                    items(
                        items = filteredNotes,
                        key = { note -> note.id }
                    ) { note ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .animateItem() // Add item animation
                        ) {
                            NotesItem(
                                isMarking = isMarking,
                                isMarked = note in markedNoteList,
                                data = note,
                                onClick = { onNoteClick(note) },
                                onLongClick = { onNoteLongClick(note) },
                                onCheckClick = { onNoteCheckClick(note) }
                            )
                        }
                    }
                }
            }
        },
        modifier = Modifier.semantics { testTag = TestTags.NOTES_PAGE }
    )

    // Dialogs
    if (showLoadingDialog) {
        LoadingDialog(
            isOpened = true,
            onDismissCallback = { showLoadingDialog = false },
            modifier = Modifier.semantics { testTag = TestTags.LOADING_DIALOG }
        )
    }

    if (showDeleteDialog) {
        TextDialog(
            isOpened = true,
            onDismissCallback = { showDeleteDialog = false },
            onConfirmCallback = { deleteNoteList },
            modifier = Modifier.semantics { testTag = TestTags.CONFIRMATION_DIALOG }
        )
    }

    if (openBottomSheet) {
        FilterSheet(
            openBottomSheet = remember { mutableStateOf(true) },
            bottomSheetState = bottomSheetState,
            onDismiss = { openBottomSheet = false },
            sortAndOrderData = viewModel.sortAndOrderData.value
        ) { sortBy, orderBy ->
            viewModel.sortAndOrder(sortBy, orderBy)
        }
    }
}

@Preview
@Composable
fun NotesPagePreview() {
    NotesPage(
        navHostController = rememberNavController(),
        viewModel = NotesPageMockVM()
    ) {

    }
}