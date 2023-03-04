package com.digiventure.ventnote.feature.notes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.digiventure.ventnote.R
import com.digiventure.ventnote.feature.notes.viewmodel.NotesPageViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesPage(
    navHostController: NavHostController,
    viewmodel: NotesPageViewModel = hiltViewModel()
) {
    val noteListState = viewmodel.noteList.observeAsState()

    Scaffold(
        topBar = {
            NotesAppBar()
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {  }, modifier = Modifier.semantics {
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
                LazyColumn(modifier = Modifier
                    .semantics { testTag = "notes-rv" }
                    .fillMaxSize()) {
                    items(10) {}
                }
            }
        }
    )
}

@Composable
fun NotesAppBar() {
    SmallTopAppBar(
        title = {
            Text(
                text = "VentNote",
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 8.dp),
            )
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        navigationIcon = {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = stringResource(R.string.fab),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 16.dp),
            )
        },
        modifier = Modifier.semantics {
            testTag = "top-appBar"
        }
    )
}