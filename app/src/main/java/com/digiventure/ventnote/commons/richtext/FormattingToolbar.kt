package com.digiventure.ventnote.commons.richtext

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Formatting toolbar with toggle buttons for bold, italic, underline, and bullet list.
 * Uses text-based buttons to avoid the Material Icons Extended dependency.
 */
@Composable
fun FormattingToolbar(
    richTextState: RichTextState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FormatToggleButton(
            label = "B",
            fontWeight = FontWeight.ExtraBold,
            isActive = richTextState.isStyleActive(RichTextStyle.Bold),
            onClick = { richTextState.toggleStyle(RichTextStyle.Bold) }
        )

        FormatToggleButton(
            label = "I",
            fontStyle = FontStyle.Italic,
            isActive = richTextState.isStyleActive(RichTextStyle.Italic),
            onClick = { richTextState.toggleStyle(RichTextStyle.Italic) }
        )

        FormatToggleButton(
            label = "U",
            textDecoration = TextDecoration.Underline,
            isActive = richTextState.isStyleActive(RichTextStyle.Underline),
            onClick = { richTextState.toggleStyle(RichTextStyle.Underline) }
        )

        FormatToggleButton(
            label = "•",
            fontSize = 20,
            isActive = richTextState.isBulletListActive(),
            onClick = { richTextState.toggleBulletList() }
        )
    }
}

@Composable
private fun FormatToggleButton(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    fontWeight: FontWeight = FontWeight.SemiBold,
    fontStyle: FontStyle = FontStyle.Normal,
    textDecoration: TextDecoration = TextDecoration.None,
    fontSize: Int = 18
) {
    val haptics = LocalHapticFeedback.current

    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.01f)
        },
        animationSpec = tween(200),
        label = "format_button_bg"
    )

    val textColor by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(200),
        label = "format_button_text"
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor, RoundedCornerShape(10.dp))
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            textDecoration = textDecoration,
            color = textColor
        )
    }
}
