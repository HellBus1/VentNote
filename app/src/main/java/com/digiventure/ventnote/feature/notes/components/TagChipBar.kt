package com.digiventure.ventnote.feature.notes.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.digiventure.ventnote.data.persistence.TagModel
import com.digiventure.ventnote.feature.tag_manager.components.TagChip
import com.digiventure.ventnote.feature.tag_manager.components.parseColor

/**
 * WhatsApp-style horizontal chip bar for filtering notes by tag.
 *
 * @param tags            All available tags.
 * @param selectedTagId   Currently selected tag ID, or null for "All".
 * @param onTagSelected   Callback when a tag chip is tapped. null = "All".
 * @param onAddTag        Callback when the "+" add-tag chip is tapped.
 */
@Composable
fun TagChipBar(
    tags: List<TagModel>,
    selectedTagId: Int?,
    onTagSelected: (tagId: Int?) -> Unit,
    onAddTag: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "All" chip
        item(key = "all") {
            AllChip(
                isSelected = selectedTagId == null,
                onClick = { onTagSelected(null) }
            )
        }

        // One chip per tag
        items(items = tags, key = { it.id }) { tag ->
            TagChip(
                tag = tag,
                isSelected = selectedTagId == tag.id,
                onClick = {
                    onTagSelected(if (selectedTagId == tag.id) null else tag.id)
                }
            )
        }

        // "+" manage-tags chip
        item(key = "add") {
            AddTagChip(onClick = onAddTag)
        }
    }
}

@Composable
private fun AllChip(isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = "All",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            )
        },
        shape = RoundedCornerShape(50),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun AddTagChip(onClick: () -> Unit) {
    FilterChip(
        selected = false,
        onClick = onClick,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Manage Tags",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Tags",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
            }
        },
        shape = RoundedCornerShape(50),
        modifier = Modifier.padding(end = 4.dp)
    )
}
