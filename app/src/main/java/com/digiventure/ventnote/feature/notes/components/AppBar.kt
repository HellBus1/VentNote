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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.components.navbar.TopNavBarIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesAppBar(
    isMarking: Boolean,
    markedNoteListSize: Int,
    isSearching: Boolean,
    searchedTitle: String,
    toggleDrawerCallback: () -> Unit,
    selectAllCallback: () -> Unit,
    unSelectAllCallback: () -> Unit,
    onSearchValueChange: (String) -> Unit,
    closeMarkingCallback: () -> Unit,
    searchCallback: () -> Unit,
    deleteCallback: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val expanded = remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (isMarking) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .clickable { expanded.value = true }
                    .semantics { testTag = TestTags.SELECTED_COUNT_CONTAINER }) {
                    NavText(
                        text = markedNoteListSize.toString(),
                        size = 16.sp,
                        modifier = Modifier
                            .padding(start = 8.dp)
                    )
                    NavText(
                        text = stringResource(R.string.selected_text),
                        size = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = stringResource(R.string.dropdown_nav_icon), tint = MaterialTheme.colorScheme.onPrimary)
                }

                DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false },
                    modifier = Modifier.semantics { testTag = TestTags.DROPDOWN_SELECT }) {
                    DropdownMenuItem(
                        text = { Text(
                            text = stringResource(R.string.select_all),
                            fontSize = 16.sp)
                        },
                        onClick = {
                            selectAllCallback()
                            expanded.value = false
                        },
                        modifier = Modifier.semantics { testTag = TestTags.SELECT_ALL_OPTION }
                    )
                    Divider()
                    DropdownMenuItem(
                        text =  { Text(
                            text = stringResource(R.string.unselect_all),
                            fontSize = 16.sp)
                        },
                        onClick = {
                            unSelectAllCallback()
                            expanded.value = false
                        },
                        modifier = Modifier.semantics { testTag = TestTags.UNSELECT_ALL_OPTION }
                    )
                }
            } else if (isSearching) {
                TextField(
                    value = searchedTitle,
                    onValueChange = {
                        onSearchValueChange(it)
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
                    modifier = Modifier
                        .padding(bottom = 0.dp)
                        .semantics { testTag = TestTags.TOP_APPBAR_TEXTFIELD },
                    placeholder = {
                        NavText(
                            text = stringResource(R.string.search_textField),
                            size = 16.sp,
                            modifier = Modifier.semantics {  })
                    }
                )
            } else {
                Text(
                    text = stringResource(id = R.string.title),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .semantics { testTag = TestTags.TOP_APPBAR_TITLE },
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        navigationIcon = {
            LeadingIcon(
                isMarking = isMarking,
                closeMarkingCallback = {
                    closeMarkingCallback()
                },
                toggleDrawerCallback = { toggleDrawerCallback() }
            )
        },
        actions = {
            TrailingMenuIcons(
                isMarking = isMarking,
                markedItemsCount = markedNoteListSize,
                isSearching = isSearching,
                searchCallback = {
                    searchCallback()
                    focusManager.clearFocus()
                },
                deleteCallback = {
                    deleteCallback()
                })
        },
        modifier = Modifier.semantics {
            testTag = TestTags.TOP_APPBAR
        }
    )
}

@Composable
fun LeadingIcon(isMarking: Boolean, closeMarkingCallback: () -> Unit, toggleDrawerCallback: () -> Unit) {
    if (isMarking) {
        TopNavBarIcon(Icons.Filled.Close, stringResource(R.string.close_nav_icon),
            modifier = Modifier.semantics { testTag = TestTags.CLOSE_SELECT_ICON_BUTTON }) {
            closeMarkingCallback()
        }
    }
    else {
        TopNavBarIcon(Icons.Filled.Menu, stringResource(R.string.drawer_nav_icon),
            modifier = Modifier.semantics { testTag = TestTags.MENU_ICON_BUTTON }) {
            toggleDrawerCallback()
        }
    }
}

@Composable
fun TrailingMenuIcons(isMarking: Boolean, markedItemsCount: Int, isSearching: Boolean, searchCallback: () -> Unit, deleteCallback: () -> Unit) {
    if (isMarking) {
        TopNavBarIcon(Icons.Filled.Delete, stringResource(R.string.delete_nav_icon),
            tint = if (markedItemsCount > 0) MaterialTheme.colorScheme.onPrimary else Color.Gray,
            modifier = Modifier.semantics { testTag = TestTags.DELETE_ICON_BUTTON }
        ) {
            if (markedItemsCount > 0) deleteCallback()
        }
    } else if (isSearching) {
        TopNavBarIcon(Icons.Filled.Close, stringResource(R.string.delete_nav_icon),
            modifier = Modifier.semantics { testTag = TestTags.CLOSE_SEARCH_ICON_BUTTON }) {
            searchCallback()
        }
    } else {
        TopNavBarIcon(Icons.Filled.Search, stringResource(R.string.search_nav_icon),
            modifier = Modifier.semantics { testTag = TestTags.SEARCH_ICON_BUTTON }) {
            searchCallback()
        }
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