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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp

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
    val tintColor = MaterialTheme.colorScheme.primary
    val chipShape = RoundedCornerShape(50)
    val bgAlpha = if (isSelected) 0.20f else 0.12f
    val borderAlpha = if (isSelected) 1f else 0.4f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(chipShape)
            .background(tintColor.copy(alpha = bgAlpha))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = tintColor.copy(alpha = borderAlpha),
                shape = chipShape
            )
            .clickable { onClick() }
            .padding(start = 12.dp, end = 12.dp, top = 5.dp, bottom = 5.dp)
    ) {
        Text(
            text = "All",
            style = MaterialTheme.typography.labelMedium.copy(
                color = tintColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun AddTagChip(onClick: () -> Unit) {
    val tintColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val chipShape = RoundedCornerShape(50)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(chipShape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            .border(
                width = 1.dp,
                color = tintColor.copy(alpha = 0.3f),
                shape = chipShape
            )
            .clickable { onClick() }
            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Manage Tags",
            modifier = Modifier.size(12.dp),
            tint = tintColor
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Tags",
            style = MaterialTheme.typography.labelMedium.copy(
                color = tintColor,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
        )
    }
}
