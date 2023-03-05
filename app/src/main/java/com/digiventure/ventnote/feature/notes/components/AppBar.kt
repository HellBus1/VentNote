package com.digiventure.ventnote.feature.notes.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digiventure.ventnote.R
import com.digiventure.ventnote.feature.notes.viewmodel.NotesPageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesAppBar(viewModel: NotesPageViewModel, toggleDrawerCallback: () -> Unit) {
    val focusManager = LocalFocusManager.current

    TopAppBar(
        title = {
            if (viewModel.isSearching.value) {
                TextField(
                    value = viewModel.searchedTitleText.value,
                    onValueChange = {
                        viewModel.searchedTitleText.value = it
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
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
                toggleDrawerCallback()
            }
        },
        actions = {
            TopNavBarIcon(Icons.Filled.Search, stringResource(R.string.fab), Modifier.semantics {  }) {
                viewModel.isSearching.value = !viewModel.isSearching.value
                viewModel.searchedTitleText.value = ""
                focusManager.clearFocus()
            }
        },
        modifier = Modifier.semantics {
            testTag = "top-appBar"
        }
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