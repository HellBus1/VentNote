package com.digiventure.ventnote.feature.notes.components.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.commons.TestTags
import com.digiventure.ventnote.components.bottomSheet.RegularBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    openBottomSheet: Boolean,
    bottomSheetState: SheetState,
    onDismiss: () -> Unit,
    sortAndOrderData: Pair<String, String>?,
    onFilter: (sortBy: String, orderBy: String) -> Unit
) {
    // Memoized sort options with constants mapping
    val sortOptions = remember {
        listOf(
            SortOption(R.string.sort_title, Constants.TITLE, Icons.Outlined.Title),
            SortOption(R.string.sort_created_date, Constants.CREATED_AT, Icons.Outlined.DateRange),
            SortOption(R.string.sort_modified_date, Constants.UPDATED_AT, Icons.Outlined.Update)
        )
    }

    val orderOptions = remember {
        listOf(
            OrderOption(R.string.order_ascending, Constants.ASCENDING, Icons.Outlined.ArrowUpward),
            OrderOption(R.string.order_descending, Constants.DESCENDING, Icons.Outlined.ArrowDownward)
        )
    }

    var selectedSortBy by remember { mutableStateOf(sortAndOrderData?.first) }
    var selectedOrderBy by remember { mutableStateOf(sortAndOrderData?.second)}

    RegularBottomSheet(
        isOpened = openBottomSheet,
        bottomSheetState = bottomSheetState,
        modifier = Modifier.semantics {
            testTag = TestTags.BOTTOM_SHEET
        },
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            FilterSection(
                title = stringResource(R.string.sort_by),
                icon = Icons.AutoMirrored.Outlined.Sort
            ) {
                LazyRow (
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(sortOptions) { option ->
                        CompactFilterChip(
                            label = stringResource(option.labelRes),
                            icon = option.icon,
                            selected = selectedSortBy == option.value,
                            onClick = { selectedSortBy = option.value }
                        )
                    }
                }
            }

            // Order By Section
            FilterSection(
                title = stringResource(R.string.order_by),
                icon = Icons.Outlined.SwapVert
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(orderOptions) { option ->
                        CompactFilterChip(
                            label = stringResource(option.labelRes),
                            icon = option.icon,
                            selected = selectedOrderBy == option.value,
                            onClick = { selectedOrderBy = option.value }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton (
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.dismiss),
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = {
                        onFilter(
                            selectedSortBy ?: Constants.CREATED_AT,
                            selectedOrderBy ?: Constants.DESCENDING
                        )
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.confirm),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        content()
    }
}

@Composable
private fun CompactFilterChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },
        selected = selected,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
        ),
    )
}

// Data classes for type safety and better organization
private data class SortOption(
    val labelRes: Int,
    val value: String,
    val icon: ImageVector
)

private data class OrderOption(
    val labelRes: Int,
    val value: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun FilterSheetPreview() {
    val openBottomSheet by rememberSaveable { mutableStateOf(true) }
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    Scaffold(
        content = { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {}
        }
    )

    FilterSheet(
        openBottomSheet = openBottomSheet,
        bottomSheetState = bottomSheetState,
        onDismiss = {},
        sortAndOrderData = null
    ) { _, _ ->

    }
}