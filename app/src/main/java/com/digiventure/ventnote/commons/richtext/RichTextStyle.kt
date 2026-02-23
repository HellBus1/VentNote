package com.digiventure.ventnote.commons.richtext

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

/**
 * Supported rich text styles for the editor.
 * Each style defines its markdown markers and corresponding SpanStyle.
 */
enum class RichTextStyle(
    val tag: String,
    val spanStyle: SpanStyle
) {
    Bold(
        tag = "BOLD",
        spanStyle = SpanStyle(fontWeight = FontWeight.Bold)
    ),
    Italic(
        tag = "ITALIC",
        spanStyle = SpanStyle(fontStyle = FontStyle.Italic)
    ),
    Underline(
        tag = "UNDERLINE",
        spanStyle = SpanStyle(textDecoration = TextDecoration.Underline)
    );

    companion object {
        fun fromTag(tag: String): RichTextStyle? = entries.find { it.tag == tag }
    }
}
