package com.digiventure.ventnote.feature.notes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.digiventure.ventnote.R
import com.digiventure.ventnote.feature.notes.viewmodel.NotesPageViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.digiventure.ventnote.commons.DateUtil
import com.digiventure.ventnote.data.NoteModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesPage(
    navHostController: NavHostController,
) {
    val viewModel: NotesPageViewModel = hiltViewModel()
    val noteListState = viewModel.noteList.observeAsState()

    Scaffold(
        topBar = {
            NotesAppBar(viewModel)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {  },
                modifier = Modifier.semantics {
                testTag = "add-note"
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
                    items(items = noteListState.value?.getOrNull() ?: listOf()) {
                        NotesItem(data = it) {
                            navHostController.navigate(route = "play_list_detail_page/${it.id}")
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun NotesAppBar(viewModel: NotesPageViewModel) {
    val focusManager = LocalFocusManager.current

    SmallTopAppBar(
        title = {
            if (viewModel.isSearching.value) {
                TextField(
                    value = viewModel.searchedTitleText.value,
                    onValueChange = {
                        viewModel.setSearchedTitleText(it)
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        textColor = MaterialTheme.colorScheme.onPrimary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                        cursorColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp,
                        lineHeight = 0.sp
                    ),
                    singleLine = true,
                    modifier = Modifier.padding(bottom = 0.dp)
                )
            } else {
                Text(
                    text = "VentNote",
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        navigationIcon = {
            TopNavBarIcon(Icons.Filled.Menu, stringResource(R.string.fab), Modifier.semantics {  }) {

            }
        },
        actions = {
            TopNavBarIcon(Icons.Filled.Search, stringResource(R.string.fab), Modifier.semantics {  }) {
                viewModel.toggleIsSearching()
                viewModel.setSearchedTitleText("")
                focusManager.clearFocus()
            }
            TopNavBarIcon(Icons.Filled.MoreVert, stringResource(R.string.fab), Modifier.semantics {  }) {

            }
        },
        modifier = Modifier.semantics {
            testTag = "top-appBar"
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesItem(data: NoteModel, callback: () -> Unit) {
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(5.dp)
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

@Composable
fun TopNavBarIcon(image: ImageVector, description: String, modifier: Modifier, onClick: () -> Unit) {
    IconButton(onClick = { onClick() }, modifier = modifier) {
        Icon(
            imageVector = image,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.onPrimary,
        )
    }
}