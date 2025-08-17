package com.digiventure.ventnote.feature.notes.components.navbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.TestTags

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesAppBar(
    isMarking: Boolean,
    markedNoteListSize: Int,
    totalNotesCount: Int = 0,
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
                SelectionTitle(
                    markedNoteListSize = markedNoteListSize,
                    totalNotesCount = totalNotesCount,
                    expanded = expanded.value,
                    onToggleExpanded = { expanded.value = !expanded.value },
                    onSelectAll = {
                        selectAllCallback()
                        expanded.value = false
                    },
                    onUnselectAll = {
                        unSelectAllCallback()
                        expanded.value = false
                    },
                    onDismiss = { expanded.value = false }
                )
            } else {
                AppTitle()
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        navigationIcon = {
            LeadingIcon(
                isMarking = isMarking,
                closeMarkingCallback = closeMarkingCallback,
                toggleDrawerCallback = toggleDrawerCallback
            )
        },
        actions = {
            TrailingMenuIcons(
                isMarking = isMarking,
                markedItemsCount = markedNoteListSize,
                sortCallback = sortCallback,
                deleteCallback = deleteCallback
            )
        },
        modifier = Modifier.semantics {
            testTag = TestTags.TOP_APPBAR
        }
    )
}

@Composable
private fun SelectionTitle(
    markedNoteListSize: Int,
    totalNotesCount: Int,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onSelectAll: () -> Unit,
    onUnselectAll: () -> Unit,
    onDismiss: () -> Unit
) {
    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                .semantics { testTag = TestTags.SELECTED_COUNT_CONTAINER }
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onToggleExpanded()
                }
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        append(markedNoteListSize.toString())
                    }
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        append(" of $totalNotesCount selected")
                    }
                },
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.dropdown_nav_icon),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        EnhancedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
            markedNoteListSize = markedNoteListSize,
            totalNotesCount = totalNotesCount,
            onSelectAll = onSelectAll,
            onUnselectAll = onUnselectAll
        )
    }
}

@Composable
private fun EnhancedDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    markedNoteListSize: Int,
    totalNotesCount: Int,
    onSelectAll: () -> Unit,
    onUnselectAll: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .semantics { testTag = TestTags.DROPDOWN_SELECT }
            .wrapContentWidth()
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 8.dp
    ) {
        val allSelected = markedNoteListSize == totalNotesCount && totalNotesCount > 0
        val noneSelected = markedNoteListSize == 0

        // Select All Option
        DropdownMenuItem(
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (allSelected) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                        contentDescription = null,
                        tint = if (allSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.select_all),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        )
                        if (totalNotesCount > 0) {
                            Text(
                                text = "Select all $totalNotesCount notes",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                }
            },
            onClick = onSelectAll,
            enabled = !allSelected,
            modifier = Modifier
                .semantics { testTag = TestTags.SELECT_ALL_OPTION }
                .padding(horizontal = 4.dp),
            colors = MenuDefaults.itemColors(
                textColor = if (allSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface
            )
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Unselect All Option
        DropdownMenuItem(
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (noneSelected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.unselect_all),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        )
                        if (markedNoteListSize > 0) {
                            Text(
                                text = "Clear selection of $markedNoteListSize notes",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                }
            },
            onClick = onUnselectAll,
            enabled = !noneSelected,
            modifier = Modifier
                .semantics { testTag = TestTags.UNSELECT_ALL_OPTION }
                .padding(horizontal = 4.dp),
            colors = MenuDefaults.itemColors(
                textColor = if (noneSelected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
private fun AppTitle() {
    Text(
        text = stringResource(id = R.string.title),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.SemiBold
        ),
        modifier = Modifier.semantics { testTag = TestTags.TOP_APPBAR_TITLE },
    )
}

@Composable
fun LeadingIcon(
    isMarking: Boolean,
    closeMarkingCallback: () -> Unit,
    toggleDrawerCallback: () -> Unit
) {
    // Option 1: Use separate variables (cleaner and more readable)
    if (isMarking) {
        IconButton(
            onClick = closeMarkingCallback,
            modifier = Modifier.semantics { testTag = TestTags.CLOSE_SELECT_ICON_BUTTON }
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.close_nav_icon),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    } else {
        IconButton(
            onClick = toggleDrawerCallback,
            modifier = Modifier.semantics { testTag = TestTags.MENU_ICON_BUTTON }
        ) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = stringResource(R.string.drawer_nav_icon),
                tint = MaterialTheme.colorScheme.onSurface
            )
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
        val deleteEnabled = markedItemsCount > 0

        IconButton(
            onClick = { if (deleteEnabled) deleteCallback() },
            modifier = Modifier.semantics { testTag = TestTags.DELETE_ICON_BUTTON }
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(R.string.delete_nav_icon),
                tint = if (deleteEnabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                }
            )
        }
    } else {
        IconButton(
            onClick = sortCallback,
            modifier = Modifier.semantics { testTag = TestTags.SORT_ICON_BUTTON }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Sort,
                contentDescription = stringResource(R.string.sort_nav_icon),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}