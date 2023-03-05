package com.digiventure.ventnote.feature.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.digiventure.ventnote.R
import com.digiventure.ventnote.feature.notes.viewmodel.NotesPageViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.digiventure.ventnote.commons.DateUtil
import com.digiventure.ventnote.data.local.NoteModel
import com.digiventure.ventnote.feature.notes.components.NotesAppBar
import com.digiventure.ventnote.feature.notes.components.NavDrawer
import kotlinx.coroutines.launch

@Composable
fun NotesPage(
    navHostController: NavHostController,
    viewModel: NotesPageViewModel = hiltViewModel()
) {
    val noteListState = viewModel.noteList.observeAsState()

    val filteredNoteListState = remember { mutableStateOf<List<NoteModel>>(listOf()) }

    LaunchedEffect(noteListState.value, viewModel.searchedTitleText.value) {
        filteredNoteListState.value = noteListState.value?.getOrNull()?.filter { note ->
            note.title.contains(viewModel.searchedTitleText.value, true)
        } ?: listOf()
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    NavDrawer(
        drawerState = drawerState,
        content = {
            Scaffold(
                topBar = {
                    NotesAppBar(viewModel, toggleDrawerCallback = {
                        scope.launch {
                            drawerState.open()
                        }
                    })
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {  },
                        modifier = Modifier.semantics {
                            testTag = "add-note-fab"
                        }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(R.string.fab),

                            )
                    }
                },
                content = { contentPadding ->
                    Box(modifier = Modifier.padding(contentPadding)) {
                        LazyColumn(
                            modifier = Modifier
                                .semantics { testTag = "notes-rv" }
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(
                                top = 24.dp,
                                bottom = 96.dp
                            )
                        ) {
                            items(items = filteredNoteListState.value) {
                                NotesItem(data = it) {
                                    navHostController.navigate(route = "")
                                }
                            }
                        }
                    }
                },
            )
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesItem(data: NoteModel, callback: () -> Unit) {
    Box(modifier = Modifier.padding(horizontal = 16.dp)
        .combinedClickable(
            onClick = {},
            onLongClick = {}
        )
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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