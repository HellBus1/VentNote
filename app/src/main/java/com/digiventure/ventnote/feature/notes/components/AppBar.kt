package com.digiventure.ventnote.feature.notes.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digiventure.ventnote.R
import com.digiventure.ventnote.components.dialog.TextDialog
import com.digiventure.ventnote.feature.notes.viewmodel.NotesPageViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesAppBar(viewModel: NotesPageViewModel, toggleDrawerCallback: () -> Unit, showSnackbar: (message: String) -> Unit) {
    val focusManager = LocalFocusManager.current
    val expanded = remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    TopAppBar(
        title = {
            if (viewModel.isMarking.value) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { expanded.value = true }) {
                    NavText(
                        text = viewModel.markedNoteList.size.toString(),
                        size = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    NavText(
                        text = "Selected",
                        size = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = stringResource(R.string.dropdown_nav_icon), tint = MaterialTheme.colorScheme.onPrimary)
                }

                DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                    DropdownMenuItem(
                        text = { Text(
                            text = "Select All",
                            fontSize = 16.sp,
                            modifier = Modifier.semantics {  })
                        },
                        onClick = {
                            viewModel.noteList.value?.getOrNull().let {
                                if (it != null) viewModel.markAllNote(it)
                            }
                            expanded.value = false
                        },
                    )
                    Divider()
                    DropdownMenuItem(
                        text =  { Text(
                            text = "Unselect All",
                            fontSize = 16.sp,
                            modifier = Modifier.semantics {  })
                        },
                        onClick = {
                            viewModel.unMarkAllNote()
                            expanded.value = false
                        },
                    )
                }
            } else if (viewModel.isSearching.value) {
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
                    modifier = Modifier.padding(bottom = 0.dp),
                    placeholder = {
                        NavText(
                            text = "Input title here",
                            size = 16.sp,
                            modifier = Modifier.semantics {  })
                    }
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
            LeadingIcon(
                isMarking = viewModel.isMarking.value,
                closeMarkingCallback = {
                    // Close marking state and clear marked notes
                    viewModel.isMarking.value = false
                    viewModel.markedNoteList.clear()
                },
                toggleDrawerCallback = { toggleDrawerCallback() })
        },
        actions = {
            TrailingMenuIcons(
                isMarking = viewModel.isMarking.value,
                markedItemsCount = viewModel.markedNoteList.size,
                isSearching = viewModel.isSearching.value,
                searchCallback = {
                    viewModel.isSearching.value = !viewModel.isSearching.value
                    viewModel.searchedTitleText.value = ""
                    focusManager.clearFocus()
                },
                deleteCallback = {
                    openDialog.value = true
                })
        },
        modifier = Modifier.semantics {
            testTag = "top-appBar"
        }
    )

    TextDialog(isOpened = openDialog.value, onDismissCallback = { openDialog.value = false }, onConfirmCallback = {
        scope.launch {
            val result = viewModel.deleteNoteList()

            openDialog.value = false
            if (result.isSuccess) {
                viewModel.unMarkAllNote()
            } else {
                result.getOrElse { error ->
                    openDialog.value = false
                    showSnackbar(error.message ?: "")
                }
            }
        }
    })
}

@Composable
fun LeadingIcon(isMarking: Boolean, closeMarkingCallback: () -> Unit, toggleDrawerCallback: () -> Unit) {
    if (isMarking) {
        TopNavBarIcon(Icons.Filled.Close, stringResource(R.string.close_nav_icon), Modifier.semantics {  }) {
            closeMarkingCallback()
        }
    } else {
        TopNavBarIcon(Icons.Filled.Menu, stringResource(R.string.drawer_nav_icon), Modifier.semantics {  }) {
            toggleDrawerCallback()
        }
    }
}

@Composable
fun TrailingMenuIcons(isMarking: Boolean, markedItemsCount: Int, isSearching: Boolean, searchCallback: () -> Unit, deleteCallback: () -> Unit) {
    if (isMarking) {
        TopNavBarIcon(Icons.Filled.Delete, stringResource(R.string.delete_nav_icon), Modifier.semantics {  },
            tint = if (markedItemsCount > 0) MaterialTheme.colorScheme.onPrimary else Color.Gray) {
            if (markedItemsCount > 0) deleteCallback()
        }
    } else if (isSearching) {
        TopNavBarIcon(Icons.Filled.Close, stringResource(R.string.delete_nav_icon), Modifier.semantics {  }) {
            searchCallback()
        }
    } else {
        TopNavBarIcon(Icons.Filled.Search, stringResource(R.string.search_nav_icon), Modifier.semantics {  }) {
            searchCallback()
        }
    }
}

@Composable
fun TopNavBarIcon(
    image: ImageVector,
    description: String,
    modifier: Modifier,
    tint: Color = MaterialTheme.colorScheme.onPrimary,
    onClick: () -> Unit,
) {
    IconButton(onClick = { onClick() }, modifier = modifier) {
        Icon(
            imageVector = image,
            contentDescription = description,
            tint = tint,
        )
    }
}

@Composable
fun NavText(text: String, size: TextUnit, modifier: Modifier) {
    Text(
        text = text,
        fontSize = size,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
    )
}