package com.digiventure.ventnote.commons.richtext

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

/**
 * Two-way converter between markdown String and AnnotatedString.
 *
 * Supported markdown subset:
 * - **bold** → Bold
 * - *italic* → Italic
 * - __underline__ → Underline
 * - "- " line prefix → Bullet list (stored as annotation tag)
 */
object MarkdownParser {

    const val BULLET_TAG = "BULLET"

    /**
     * Parse a markdown string into an AnnotatedString with proper SpanStyles.
     */
    fun parseToAnnotatedString(markdown: String): AnnotatedString {
        return buildAnnotatedString {
            val lines = markdown.split("\n")
            lines.forEachIndexed { lineIndex, line ->
                val isBullet = line.startsWith("- ")
                val contentLine = if (isBullet) line.removePrefix("- ") else line

                if (isBullet) {
                    // Add bullet annotation for the entire line range
                    val startOffset = length
                    append("•  ")
                    parseInlineStyles(this, contentLine)
                    addStringAnnotation(
                        tag = BULLET_TAG,
                        annotation = "true",
                        start = startOffset,
                        end = length
                    )
                } else {
                    parseInlineStyles(this, contentLine)
                }

                if (lineIndex < lines.lastIndex) {
                    append("\n")
                }
            }
        }
    }

    /**
     * Convert an AnnotatedString back to markdown string for storage.
     */
    fun toMarkdown(annotatedString: AnnotatedString): String {
        val text = annotatedString.text
        if (text.isEmpty()) return ""

        val lines = text.split("\n")
        val result = StringBuilder()
        var charOffset = 0

        lines.forEachIndexed { lineIndex, line ->
            val lineStart = charOffset
            val lineEnd = charOffset + line.length

            // Check if this line has a bullet annotation
            val isBullet = annotatedString.getStringAnnotations(
                tag = BULLET_TAG,
                start = lineStart,
                end = lineEnd
            ).isNotEmpty()

            val displayLine = if (isBullet && line.startsWith("•  ")) {
                line.removePrefix("•  ")
            } else if (isBullet && line.startsWith("• ")) {
                line.removePrefix("• ")
            } else {
                line
            }

            if (isBullet) {
                result.append("- ")
            }

            // Build the markdown for this line by processing characters
            val lineContentStart = if (isBullet) lineStart + (line.length - displayLine.length) else lineStart
            result.append(buildLineMarkdown(annotatedString, displayLine, lineContentStart))

            if (lineIndex < lines.lastIndex) {
                result.append("\n")
            }

            charOffset = lineEnd + 1 // +1 for newline
        }

        return result.toString()
    }

    // ─────────────────────────── Internal helpers ───────────────────────────

    /**
     * Parse inline markdown styles (**bold**, *italic*, __underline__) and append to builder.
     */
    private fun parseInlineStyles(builder: AnnotatedString.Builder, text: String) {
        var i = 0
        while (i < text.length) {
            when {
                // Bold+Italic: ***text***
                text.startsWith("***", i) -> {
                    val end = text.indexOf("***", i + 3)
                    if (end != -1) {
                        val content = text.substring(i + 3, end)
                        val startOffset = builder.length
                        builder.append(content)
                        builder.addStyle(RichTextStyle.Bold.spanStyle, startOffset, builder.length)
                        builder.addStringAnnotation(RichTextStyle.Bold.tag, "", startOffset, builder.length)
                        builder.addStyle(RichTextStyle.Italic.spanStyle, startOffset, builder.length)
                        builder.addStringAnnotation(RichTextStyle.Italic.tag, "", startOffset, builder.length)
                        i = end + 3
                    } else {
                        builder.append(text[i])
                        i++
                    }
                }
                // Underline: __text__
                text.startsWith("__", i) -> {
                    val end = text.indexOf("__", i + 2)
                    if (end != -1) {
                        val content = text.substring(i + 2, end)
                        val startOffset = builder.length
                        builder.append(content)
                        builder.addStyle(RichTextStyle.Underline.spanStyle, startOffset, builder.length)
                        builder.addStringAnnotation(RichTextStyle.Underline.tag, "", startOffset, builder.length)
                        i = end + 2
                    } else {
                        builder.append(text[i])
                        i++
                    }
                }
                // Bold: **text**
                text.startsWith("**", i) -> {
                    val end = text.indexOf("**", i + 2)
                    if (end != -1) {
                        val content = text.substring(i + 2, end)
                        val startOffset = builder.length
                        builder.append(content)
                        builder.addStyle(RichTextStyle.Bold.spanStyle, startOffset, builder.length)
                        builder.addStringAnnotation(RichTextStyle.Bold.tag, "", startOffset, builder.length)
                        i = end + 2
                    } else {
                        builder.append(text[i])
                        i++
                    }
                }
                // Italic: *text*
                text.startsWith("*", i) -> {
                    val end = text.indexOf("*", i + 1)
                    if (end != -1) {
                        val content = text.substring(i + 1, end)
                        val startOffset = builder.length
                        builder.append(content)
                        builder.addStyle(RichTextStyle.Italic.spanStyle, startOffset, builder.length)
                        builder.addStringAnnotation(RichTextStyle.Italic.tag, "", startOffset, builder.length)
                        i = end + 1
                    } else {
                        builder.append(text[i])
                        i++
                    }
                }
                else -> {
                    builder.append(text[i])
                    i++
                }
            }
        }
    }

    /**
     * Build markdown string for a single line content by reading span annotations.
     */
    private fun buildLineMarkdown(
        annotatedString: AnnotatedString,
        displayText: String,
        globalOffset: Int
    ): String {
        if (displayText.isEmpty()) return ""

        data class StyleRange(val start: Int, val end: Int, val style: RichTextStyle)

        val styleRanges = mutableListOf<StyleRange>()

        // Collect all style annotations within this line range
        for (style in RichTextStyle.entries) {
            val annotations = annotatedString.getStringAnnotations(
                tag = style.tag,
                start = globalOffset,
                end = globalOffset + displayText.length
            )
            for (ann in annotations) {
                val relStart = (ann.start - globalOffset).coerceIn(0, displayText.length)
                val relEnd = (ann.end - globalOffset).coerceIn(0, displayText.length)
                if (relStart < relEnd) {
                    styleRanges.add(StyleRange(relStart, relEnd, style))
                }
            }
        }

        if (styleRanges.isEmpty()) return displayText

        // Group styles by exact same range
        data class RangeKey(val start: Int, val end: Int)

        val groupedStyles = styleRanges.groupBy { RangeKey(it.start, it.end) }

        // Build result by walking through the text
        val result = StringBuilder()
        val sortedKeys = groupedStyles.keys.sortedBy { it.start }

        var pos = 0
        for (key in sortedKeys) {
            // Append unstyled text before this range
            if (key.start > pos) {
                result.append(displayText.substring(pos, key.start))
            }

            val styles = groupedStyles[key]!!.map { it.style }.toSet()
            val content = displayText.substring(key.start, key.end)

            // Handle combined styles
            val hasBold = RichTextStyle.Bold in styles
            val hasItalic = RichTextStyle.Italic in styles
            val hasUnderline = RichTextStyle.Underline in styles

            when {
                hasBold && hasItalic -> {
                    result.append("***").append(content).append("***")
                }
                hasBold -> {
                    result.append("**").append(content).append("**")
                }
                hasItalic -> {
                    result.append("*").append(content).append("*")
                }
                hasUnderline -> {
                    result.append("__").append(content).append("__")
                }
            }

            // If underline is combined with bold/italic, we'd need nesting.
            // For simplicity, underline is standalone. Combined bold+underline
            // would store as **__text__** which our parser doesn't handle.
            // This is a known limitation for v1.

            pos = key.end
        }

        // Append remaining unstyled text
        if (pos < displayText.length) {
            result.append(displayText.substring(pos))
        }

        return result.toString()
    }
}
