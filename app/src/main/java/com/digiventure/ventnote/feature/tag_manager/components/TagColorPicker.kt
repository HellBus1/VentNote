package com.digiventure.ventnote.feature.tag_manager.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Fixed 12-color palette — same palette as Google Keep. */
val TAG_COLOR_PALETTE = listOf(
    "#EF5350" to "Red",
    "#EC407A" to "Pink",
    "#AB47BC" to "Purple",
    "#5C6BC0" to "Indigo",
    "#42A5F5" to "Blue",
    "#26C6DA" to "Cyan",
    "#26A69A" to "Teal",
    "#66BB6A" to "Green",
    "#D4E157" to "Lime",
    "#FFCA28" to "Yellow",
    "#FFA726" to "Orange",
    "#8D6E63" to "Brown"
)

/**
 * A 4-column grid of color circles. Tapping a circle selects it.
 *
 * @param selectedHex  The currently selected hex color string.
 * @param onColorSelected  Callback with the hex string of the newly chosen color.
 */
@Composable
fun TagColorPicker(
    selectedHex: String,
    onColorSelected: (hex: String) -> Unit
) {
    Column {
        Text(
            text = "Color",
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(TAG_COLOR_PALETTE) { (hex, name) ->
                val color = parseColor(hex)
                val isSelected = hex.equals(selectedHex, ignoreCase = true)
                ColorCircle(
                    color = color,
                    label = name,
                    isSelected = isSelected,
                    onClick = { onColorSelected(hex) }
                )
            }
        }
    }
}

@Composable
private fun ColorCircle(
    color: Color,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 2.5.dp else 0.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                shape = CircleShape
            )
            .clickable { onClick() }
            .padding(2.dp)
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "Selected: $label",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
