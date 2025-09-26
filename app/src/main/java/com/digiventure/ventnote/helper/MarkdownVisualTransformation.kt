package com.digiventure.ventnote.helper

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Visual transformation that applies markdown styling to text input
 * Supports: bold (**text**), italic (*text*), bold+italic (***text***)
 */
class MarkdownVisualTransformation : VisualTransformation {

    companion object {
        // Regex patterns ordered by precedence (longest patterns first)
        private val BOLD_ITALIC_PATTERN = Regex("""\*\*\*(.*?)\*\*\*""")
        private val BOLD_PATTERN = Regex("""\*\*(.*?)\*\*""")
        private val ITALIC_PATTERN = Regex("""\*(.*?)\*""")
    }

    private data class StyleRange(
        val start: Int,
        val end: Int,
        val style: SpanStyle,
        val type: MarkdownType
    )

    private enum class MarkdownType {
        BOLD_ITALIC, BOLD, ITALIC
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text

        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val styleRanges = collectStyleRanges(originalText)
        val styledText = applyStyles(originalText, styleRanges)

        return TransformedText(
            text = styledText,
            offsetMapping = OffsetMapping.Identity
        )
    }

    /**
     * Collect all markdown style ranges, handling precedence and overlaps
     */
    private fun collectStyleRanges(text: String): List<StyleRange> {
        val ranges = mutableListOf<StyleRange>()

        // Process bold+italic first (highest precedence)
        BOLD_ITALIC_PATTERN.findAll(text).forEach { match ->
            ranges.add(
                StyleRange(
                    start = match.groups[1]!!.range.first,
                    end = match.groups[1]!!.range.last + 1,
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    ),
                    type = MarkdownType.BOLD_ITALIC
                )
            )
        }

        // Process bold, excluding areas already covered by bold+italic
        BOLD_PATTERN.findAll(text).forEach { match ->
            val contentRange = match.groups[1]!!.range.first..match.groups[1]!!.range.last
            if (!hasOverlapWithType(ranges, contentRange, MarkdownType.BOLD_ITALIC)) {
                ranges.add(
                    StyleRange(
                        start = contentRange.first,
                        end = contentRange.last + 1,
                        style = SpanStyle(fontWeight = FontWeight.Bold),
                        type = MarkdownType.BOLD
                    )
                )
            }
        }

        // Process italic, excluding areas already covered by bold+italic or bold
        ITALIC_PATTERN.findAll(text).forEach { match ->
            val contentRange = match.groups[1]!!.range.first..match.groups[1]!!.range.last
            if (!hasOverlapWithType(ranges, contentRange, MarkdownType.BOLD_ITALIC, MarkdownType.BOLD)) {
                ranges.add(
                    StyleRange(
                        start = contentRange.first,
                        end = contentRange.last + 1,
                        style = SpanStyle(fontStyle = FontStyle.Italic),
                        type = MarkdownType.ITALIC
                    )
                )
            }
        }

        return ranges.sortedBy { it.start }
    }

    /**
     * Check if a range overlaps with any existing ranges of specified types
     */
    private fun hasOverlapWithType(
        existingRanges: List<StyleRange>,
        newRange: IntRange,
        vararg types: MarkdownType
    ): Boolean {
        return existingRanges.any { existing ->
            types.contains(existing.type) && rangesOverlap(
                existing.start until existing.end,
                newRange
            )
        }
    }

    /**
     * Check if two ranges overlap
     */
    private fun rangesOverlap(range1: IntRange, range2: IntRange): Boolean {
        return range1.first < range2.last && range2.first < range1.last
    }

    /**
     * Apply collected styles to create the final AnnotatedString
     */
    private fun applyStyles(originalText: String, styleRanges: List<StyleRange>): AnnotatedString {
        return buildAnnotatedString {
            append(originalText)

            // Apply all collected styles
            styleRanges.forEach { range ->
                addStyle(
                    style = range.style,
                    start = range.start.coerceAtLeast(0),
                    end = range.end.coerceAtMost(originalText.length)
                )
            }
        }
    }
}