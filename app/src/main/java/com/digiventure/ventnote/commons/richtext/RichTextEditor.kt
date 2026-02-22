package com.digiventure.ventnote.commons.richtext

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Rich text editor composable that replaces the standard TextField.
 * Supports styled text (bold, italic, underline) via AnnotatedString.
 */
@Composable
fun RichTextEditor(
    richTextState: RichTextState,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    isEditing: Boolean = true,
    placeholder: String = "",
    contentDescriptionText: String = "",
    testTagText: String = "",
    minHeight: Dp = 200.dp,
    onFocusChanged: ((Boolean) -> Unit)? = null
) {
    val label = "border_color"
    val borderColor by animateColorAsState(
        targetValue = if (isEditing) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(300),
        label = label
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .heightIn(min = minHeight),
        colors = CardDefaults.cardColors(
            containerColor = if (isEditing) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isEditing) 4.dp else 0.dp
        ),
        border = BorderStroke(
            width = if (isEditing) 2.dp else 0.dp,
            color = borderColor
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
                .semantics {
                    if (contentDescriptionText.isNotEmpty()) {
                        contentDescription = contentDescriptionText
                    }
                    if (testTagText.isNotEmpty()) {
                        testTag = testTagText
                    }
                }
        ) {
            if (richTextState.toPlainText().isEmpty() && isEditing) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                )
            }

            BasicTextField(
                value = richTextState.textFieldValue,
                onValueChange = { newValue ->
                    if (!readOnly) {
                        richTextState.onTextFieldValueChange(newValue)
                    }
                },
                readOnly = readOnly,
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .onFocusChanged { focusState ->
                        onFocusChanged?.invoke(focusState.isFocused)
                    }
            )
        }
    }
}
