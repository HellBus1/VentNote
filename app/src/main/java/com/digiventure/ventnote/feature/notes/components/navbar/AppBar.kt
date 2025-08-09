package com.digiventure.ventnote.feature.notes.components.navbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
    toggleDrawerCallback: () -> Unit,
    selectAllCallback: () -> Unit,
    unSelectAllCallback: () -> Unit,
    closeMarkingCallback: () -> Unit,
    sortCallback: () -> Unit,
    deleteCallback: () -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            if (isMarking) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { expanded.value = true }
                        .semantics { testTag = TestTags.SELECTED_COUNT_CONTAINER }) {
                    NavText(
                        text = markedNoteListSize.toString(),
                        size = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    NavText(
                        text = stringResource(R.string.selected_text),
                        size = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.dropdown_nav_icon),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false },
                    modifier = Modifier.semantics { testTag = TestTags.DROPDOWN_SELECT }) {
                    DropdownMenuItem(text = {
                        Text(
                            text = stringResource(R.string.select_all),
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }, onClick = {
                        selectAllCallback()
                        expanded.value = false
                    }, modifier = Modifier.semantics { testTag = TestTags.SELECT_ALL_OPTION })
                    HorizontalDivider()
                    DropdownMenuItem(text = {
                        Text(
                            text = stringResource(R.string.unselect_all),
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }, onClick = {
                        unSelectAllCallback()
                        expanded.value = false
                    }, modifier = Modifier.semantics { testTag = TestTags.UNSELECT_ALL_OPTION })
                }
            } else {
                Text(
                    text = stringResource(id = R.string.title),
                    color = MaterialTheme.colorScheme.primary,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold, fontSize = 20.sp
                    ),
                    modifier = Modifier.semantics { testTag = TestTags.TOP_APPBAR_TITLE },
                )
            }
        },
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        navigationIcon = {
            LeadingIcon(isMarking = isMarking, closeMarkingCallback = {
                closeMarkingCallback()
            }, toggleDrawerCallback = { toggleDrawerCallback() })
        },
        actions = {
            TrailingMenuIcons(
                isMarking = isMarking,
                markedItemsCount = markedNoteListSize,
                sortCallback = {
                    sortCallback()
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
fun LeadingIcon(
    isMarking: Boolean, closeMarkingCallback: () -> Unit, toggleDrawerCallback: () -> Unit
) {
    if (isMarking) {
        TopNavBarIcon(
            Icons.Filled.Close,
            stringResource(R.string.close_nav_icon),
            modifier = Modifier.semantics { testTag = TestTags.CLOSE_SELECT_ICON_BUTTON }) {
            closeMarkingCallback()
        }
    } else {
        TopNavBarIcon(
            Icons.Filled.Menu,
            stringResource(R.string.drawer_nav_icon),
            modifier = Modifier.semantics { testTag = TestTags.MENU_ICON_BUTTON }) {
            toggleDrawerCallback()
        }
    }
}

@Composable
fun TrailingMenuIcons(
    isMarking: Boolean,
    markedItemsCount: Int,
    sortCallback: () -> Unit,
    deleteCallback: () -> Unit,
) {
    if (isMarking) {
        TopNavBarIcon(
            Icons.Filled.Delete,
            stringResource(R.string.delete_nav_icon),
            tint = if (markedItemsCount > 0) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.primary.copy(
                alpha = 0.6f
            ),
            modifier = Modifier.semantics { testTag = TestTags.DELETE_ICON_BUTTON }) {
            if (markedItemsCount > 0) deleteCallback()
        }
    } else {
        TopNavBarIcon(
            image = Icons.Outlined.MoreVert,
            stringResource(R.string.sort_nav_icon),
            modifier = Modifier.semantics { testTag = TestTags.SORT_ICON_BUTTON }) {
            sortCallback()
        }
    }
}

@Composable
fun NavText(text: String, size: TextUnit, modifier: Modifier) {
    Text(
        text = text, fontSize = size, color = MaterialTheme.colorScheme.primary, modifier = modifier
    )
}