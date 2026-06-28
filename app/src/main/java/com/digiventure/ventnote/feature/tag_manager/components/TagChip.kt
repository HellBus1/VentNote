package com.digiventure.ventnote.feature.tag_manager.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digiventure.ventnote.data.persistence.TagModel

/**
 * Reusable tag chip composable.
 *
 * @param tag         The tag to display.
 * @param isSelected  When true, the chip renders with a border/filled state.
 * @param onClick     Callback when the chip body is clicked.
 * @param onRemove    When non-null, shows an ✕ close button and calls this on press.
 */
@Composable
fun TagChip(
    tag: TagModel,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val tagColor = parseColor(tag.colorHex)
    val chipShape = RoundedCornerShape(50)
    val bgAlpha = if (isSelected) 0.20f else 0.12f
    val borderAlpha = if (isSelected) 1f else 0.4f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(30.dp)
            .clip(chipShape)
            .background(tagColor.copy(alpha = bgAlpha))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = tagColor.copy(alpha = borderAlpha),
                shape = chipShape
            )
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            )
            .padding(start = 8.dp, end = if (onRemove != null) 2.dp else 10.dp, top = 5.dp, bottom = 5.dp)
    ) {
        // Colored dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(tagColor)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = tag.name,
            style = MaterialTheme.typography.labelMedium.copy(
                color = tagColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                fontSize = 12.sp
            )
        )

        if (onRemove != null) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Remove tag ${tag.name}",
                    tint = tagColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/**
 * Parse a hex color string (e.g. "#EF5350" or "EF5350") to [Color].
 * Falls back to a neutral grey if the string is malformed.
 */
fun parseColor(hex: String): Color {
    return try {
        val cleaned = hex.trimStart('#')
        val colorLong = cleaned.toLong(16)
        val color = if (cleaned.length == 6) 0xFF000000L or colorLong else colorLong
        Color(color)
    } catch (_: Exception) {
        Color(0xFF9E9E9E)
    }
}
