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
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
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
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.commons.TestTags

val SortIcon: ImageVector
    get() = ImageVector.Builder(
        name = "SortIcon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(3f, 7f)
            horizontalLineToRelative(18f)
            curveToRelative(0.55f, 0f, 1f, -0.45f, 1f, -1f)
            reflectiveCurveTo(21.55f, 5f, 21f, 5f)
            lineTo(3f, 5f)
            curveToRelative(-0.55f, 0f, -1f, 0.45f, -1f, 1f)
            reflectiveCurveTo(2.45f, 7f, 3f, 7f)
            close()
            
            moveTo(6f, 13f)
            horizontalLineToRelative(12f)
            curveToRelative(0.55f, 0f, 1f, -0.45f, 1f, -1f)
            reflectiveCurveTo(18.55f, 11f, 18f, 11f)
            lineTo(6f, 11f)
            curveToRelative(-0.55f, 0f, -1f, 0.45f, -1f, 1f)
            reflectiveCurveTo(5.45f, 13f, 6f, 13f)
            close()
            
            moveTo(9f, 19f)
            horizontalLineToRelative(6f)
            curveToRelative(0.55f, 0f, 1f, -0.45f, 1f, -1f)
            reflectiveCurveTo(15.55f, 17f, 15f, 17f)
            lineTo(9f, 17f)
            curveToRelative(-0.55f, 0f, -1f, 0.45f, -1f, 1f)
            reflectiveCurveTo(8.45f, 19f, 9f, 19f)
            close()
        }
    }.build()

val GridViewIcon: ImageVector
    get() = ImageVector.Builder(
        name = "GridViewIcon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            // Top-left
            moveTo(3f, 3f)
            horizontalLineToRelative(6f)
            curveToRelative(0.55f, 0f, 1f, 0.45f, 1f, 1f)
            verticalLineToRelative(6f)
            curveToRelative(0f, 0.55f, -0.45f, 1f, -1f, 1f)
            lineTo(3f, 11f)
            curveToRelative(-0.55f, 0f, -1f, -0.45f, -1f, -1f)
            lineTo(2f, 4f)
            curveToRelative(0f, -0.55f, 0.45f, -1f, 1f, -1f)
            close()
            
            // Top-right
            moveTo(15f, 3f)
            horizontalLineToRelative(6f)
            curveToRelative(0.55f, 0f, 1f, 0.45f, 1f, 1f)
            verticalLineToRelative(6f)
            curveToRelative(0f, 0.55f, -0.45f, 1f, -1f, 1f)
            horizontalLineToRelative(-6f)
            curveToRelative(-0.55f, 0f, -1f, -0.45f, -1f, -1f)
            lineTo(14f, 4f)
            curveToRelative(0f, -0.55f, 0.45f, -1f, 1f, -1f)
            close()
            
            // Bottom-left
            moveTo(3f, 15f)
            horizontalLineToRelative(6f)
            curveToRelative(0.55f, 0f, 1f, 0.45f, 1f, 1f)
            verticalLineToRelative(6f)
            curveToRelative(0f, 0.55f, -0.45f, 1f, -1f, 1f)
            lineTo(3f, 23f)
            curveToRelative(-0.55f, 0f, -1f, -0.45f, -1f, -1f)
            verticalLineToRelative(-6f)
            curveToRelative(0f, -0.55f, 0.45f, -1f, 1f, -1f)
            close()
            
            // Bottom-right
            moveTo(15f, 15f)
            horizontalLineToRelative(6f)
            curveToRelative(0.55f, 0f, 1f, 0.45f, 1f, 1f)
            verticalLineToRelative(6f)
            curveToRelative(0f, 0.55f, -0.45f, 1f, -1f, 1f)
            horizontalLineToRelative(-6f)
            curveToRelative(-0.55f, 0f, -1f, -0.45f, -1f, -1f)
            verticalLineToRelative(-6f)
            curveToRelative(0f, -0.55f, 0.45f, -1f, 1f, -1f)
            close()
        }
    }.build()

val ListViewIcon: ImageVector
    get() = ImageVector.Builder(
        name = "ListViewIcon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            // Row 1
            moveTo(3f, 6f)
            curveToRelative(0f, -0.55f, 0.45f, -1f, 1f, -1f)
            horizontalLineToRelative(1f)
            curveToRelative(0.55f, 0f, 1f, 0.45f, 1f, 1f)
            verticalLineToRelative(1f)
            curveToRelative(0f, 0.55f, -0.45f, 1f, -1f, 1f)
            lineTo(4f, 9f)
            curveToRelative(-0.55f, 0f, -1f, -0.45f, -1f, -1f)
            close()
            
            moveTo(9f, 7f)
            horizontalLineToRelative(12f)
            curveToRelative(0.55f, 0f, 1f, -0.45f, 1f, -1f)
            reflectiveCurveTo(21.55f, 5f, 21f, 5f)
            lineTo(9f, 5f)
            curveToRelative(-0.55f, 0f, -1f, 0.45f, -1f, 1f)
            reflectiveCurveTo(8.45f, 7f, 9f, 7f)
            close()
            
            // Row 2
            moveTo(3f, 12f)
            curveToRelative(0f, -0.55f, 0.45f, -1f, 1f, -1f)
            horizontalLineToRelative(1f)
            curveToRelative(0.55f, 0f, 1f, 0.45f, 1f, 1f)
            verticalLineToRelative(1f)
            curveToRelative(0f, 0.55f, -0.45f, 1f, -1f, 1f)
            lineTo(4f, 15f)
            curveToRelative(-0.55f, 0f, -1f, -0.45f, -1f, -1f)
            close()
            
            moveTo(9f, 13f)
            horizontalLineToRelative(12f)
            curveToRelative(0.55f, 0f, 1f, -0.45f, 1f, -1f)
            reflectiveCurveTo(21.55f, 11f, 21f, 11f)
            lineTo(9f, 11f)
            curveToRelative(-0.55f, 0f, -1f, 0.45f, -1f, 1f)
            reflectiveCurveTo(8.45f, 13f, 9f, 13f)
            close()
            
            // Row 3
            moveTo(3f, 18f)
            curveToRelative(0f, -0.55f, 0.45f, -1f, 1f, -1f)
            horizontalLineToRelative(1f)
            curveToRelative(0.55f, 0f, 1f, 0.45f, 1f, 1f)
            verticalLineToRelative(1f)
            curveToRelative(0f, 0.55f, -0.45f, 1f, -1f, 1f)
            lineTo(4f, 21f)
            curveToRelative(-0.55f, 0f, -1f, -0.45f, -1f, -1f)
            close()
            
            moveTo(9f, 19f)
            horizontalLineToRelative(12f)
            curveToRelative(0.55f, 0f, 1f, -0.45f, 1f, -1f)
            reflectiveCurveTo(21.55f, 17f, 21f, 17f)
            lineTo(9f, 17f)
            curveToRelative(-0.55f, 0f, -1f, 1f, -1f, 1f)
            reflectiveCurveTo(8.45f, 19f, 9f, 19f)
            close()
        }
    }.build()

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
    noteViewMode: String = Constants.VIEW_MODE_LIST,
    viewModeCallback: () -> Unit = {}
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
                deleteCallback = deleteCallback,
                noteViewMode = noteViewMode,
                viewModeCallback = viewModeCallback
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
                imageVector = Icons.Rounded.Check,
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
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        append(" of $totalNotesCount selected")
                    }
                },
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
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
                        imageVector = if (allSelected) Icons.Rounded.Check else Icons.Rounded.Check,
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
                        imageVector = Icons.Rounded.Close,
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
    if (isMarking) {
        IconButton(
            onClick = closeMarkingCallback,
            modifier = Modifier.semantics { testTag = TestTags.CLOSE_SELECT_ICON_BUTTON }
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
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
                imageVector = Icons.Rounded.Menu,
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
    noteViewMode: String,
    viewModeCallback: () -> Unit
) {
    if (isMarking) {
        val deleteEnabled = markedItemsCount > 0

        IconButton(
            onClick = { if (deleteEnabled) deleteCallback() },
            modifier = Modifier.semantics { testTag = TestTags.DELETE_ICON_BUTTON }
        ) {
            Icon(
                imageVector = Icons.Rounded.Delete,
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
            onClick = viewModeCallback,
            modifier = Modifier.semantics { testTag = "view_mode_icon_button" }
        ) {
            val viewIcon = when (noteViewMode) {
                Constants.VIEW_MODE_STAGGERED -> ListViewIcon
                else -> GridViewIcon
            }
            val contentDescription = when (noteViewMode) {
                Constants.VIEW_MODE_STAGGERED -> "Switch to list view"
                else -> "Switch to grid view"
            }
            Icon(
                imageVector = viewIcon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(
            onClick = sortCallback,
            modifier = Modifier.semantics { testTag = TestTags.SORT_ICON_BUTTON }
        ) {
            Icon(
                imageVector = SortIcon,
                contentDescription = stringResource(R.string.sort_nav_icon),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}