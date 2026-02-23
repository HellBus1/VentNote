package com.digiventure.ventnote.commons.richtext

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue

/**
 * A style range tracked independently of AnnotatedString.
 * This is the source of truth for styling; AnnotatedString is rebuilt from these.
 */
data class StyleRange(
    val start: Int,
    val end: Int,
    val style: RichTextStyle
)

/**
 * State holder for the rich text editor.
 *
 * Key design: Compose's BasicTextField strips all AnnotatedString annotations
 * on every onValueChange call. So we maintain our own list of [StyleRange]s
 * as the source of truth and rebuild the AnnotatedString on every change.
 */
class RichTextState {

    /** The plain text + selection. AnnotatedString is rebuilt from [styleRanges]. */
    var textFieldValue by mutableStateOf(TextFieldValue(""))
        private set

    /** Our source of truth for styles — independent of AnnotatedString. */
    val styleRanges = mutableStateListOf<StyleRange>()

    /** Tracks which line indices are bullet list items (0-indexed). */
    val bulletLines = mutableStateListOf<Int>()

    /** Currently active toggles (applied to next typed text). */
    var activeStyles by mutableStateOf(setOf<RichTextStyle>())
        private set

    /** Flag to suppress re-processing when we set the value internally. */
    private var isInternalUpdate = false

    /**
     * Initialize from a markdown string (e.g. when loading from database).
     */
    fun setFromMarkdown(markdown: String) {
        // Parse markdown to get styled text
        val parsed = MarkdownParser.parseToAnnotatedString(markdown)

        // Extract style ranges from the parsed AnnotatedString
        styleRanges.clear()
        for (style in RichTextStyle.entries) {
            val annotations = parsed.getStringAnnotations(style.tag, 0, parsed.length)
            for (ann in annotations) {
                styleRanges.add(StyleRange(ann.start, ann.end, style))
            }
        }

        // Extract bullet lines
        bulletLines.clear()
        val lines = parsed.text.split("\n")
        lines.forEachIndexed { index, line ->
            if (line.startsWith("•  ") || line.startsWith("• ")) {
                bulletLines.add(index)
            }
        }

        isInternalUpdate = true
        textFieldValue = TextFieldValue(
            annotatedString = buildStyledText(parsed.text),
            selection = TextRange(parsed.length)
        )
        isInternalUpdate = false
        activeStyles = emptySet()
    }

    /**
     * Export the current editor content as a markdown string for storage.
     */
    fun toMarkdown(): String {
        val text = textFieldValue.text
        if (text.isEmpty()) return ""

        val lines = text.split("\n")
        val result = StringBuilder()
        var charOffset = 0

        lines.forEachIndexed { lineIndex, line ->
            val lineStart = charOffset
            val lineEnd = charOffset + line.length
            val isBullet = lineIndex in bulletLines

            // Get the content (strip bullet prefix for markdown)
            val displayContent = if (isBullet && (line.startsWith("•  ") || line.startsWith("• "))) {
                val prefix = if (line.startsWith("•  ")) "•  " else "• "
                line.removePrefix(prefix)
            } else {
                line
            }

            val contentStart = if (isBullet && (line.startsWith("•  ") || line.startsWith("• "))) {
                lineStart + (line.length - displayContent.length)
            } else {
                lineStart
            }

            if (isBullet) {
                result.append("- ")
            }

            // Build markdown for this line's content
            result.append(buildLineWithMarkdown(displayContent, contentStart))

            if (lineIndex < lines.lastIndex) {
                result.append("\n")
            }

            charOffset = lineEnd + 1
        }

        return result.toString()
    }

    /**
     * Get the plain text content (for validation checks like isEmpty).
     */
    fun toPlainText(): String = textFieldValue.text

    /**
     * Called when the text field value changes (user typing or selection change).
     */
    fun onTextFieldValueChange(newValue: TextFieldValue) {
        if (isInternalUpdate) return

        val oldText = textFieldValue.text
        val newText = newValue.text

        if (oldText != newText) {
            // Text changed — adjust our style ranges
            val lengthDiff = newText.length - oldText.length

            if (lengthDiff > 0) {
                // Text was INSERTED
                val insertPos = findInsertionPoint(oldText, newText, newValue.selection)
                val insertLen = lengthDiff
                adjustRangesForInsertion(insertPos, insertLen)
                adjustBulletLinesForInsertion(oldText, newText, insertPos, insertLen)

                // Apply active styles to the inserted text
                if (activeStyles.isNotEmpty()) {
                    for (style in activeStyles) {
                        styleRanges.add(StyleRange(insertPos, insertPos + insertLen, style))
                    }
                    mergeOverlappingRanges()
                }
            } else if (lengthDiff < 0) {
                // Text was DELETED
                val deleteLen = -lengthDiff
                val deletePos = findDeletionPoint(oldText, newText, newValue.selection)
                adjustRangesForDeletion(deletePos, deleteLen)
                adjustBulletLinesForDeletion(oldText, newText, deletePos, deleteLen)
            }

            // Rebuild the annotated string with our style ranges
            isInternalUpdate = true
            textFieldValue = TextFieldValue(
                annotatedString = buildStyledText(newText),
                selection = newValue.selection,
                composition = newValue.composition
            )
            isInternalUpdate = false
        } else {
            // Only selection/composition changed
            isInternalUpdate = true
            textFieldValue = TextFieldValue(
                annotatedString = buildStyledText(newText),
                selection = newValue.selection,
                composition = newValue.composition
            )
            isInternalUpdate = false
        }

        // Update active styles from cursor position
        updateActiveStylesFromCursor()
    }

    /**
     * Toggle a style on/off for the current selection or future typing.
     */
    fun toggleStyle(style: RichTextStyle) {
        val selection = textFieldValue.selection

        if (selection.collapsed) {
            // No selection — toggle for future typing
            activeStyles = if (style in activeStyles) {
                activeStyles - style
            } else {
                activeStyles + style
            }
        } else {
            // Has selection — apply/remove style to selected range
            val start = selection.min
            val end = selection.max
            val hasStyle = hasStyleInRange(style, start, end)

            if (hasStyle) {
                removeStyleFromRange(style, start, end)
            } else {
                styleRanges.add(StyleRange(start, end, style))
                mergeOverlappingRanges()
            }

            // Rebuild display
            isInternalUpdate = true
            textFieldValue = TextFieldValue(
                annotatedString = buildStyledText(textFieldValue.text),
                selection = selection,
                composition = textFieldValue.composition
            )
            isInternalUpdate = false
        }
    }

    /**
     * Toggle bullet list for the current line.
     */
    fun toggleBulletList() {
        val text = textFieldValue.text
        val cursorPos = textFieldValue.selection.start.coerceIn(0, text.length)

        // Find current line index
        val lineIndex = text.substring(0, cursorPos).count { it == '\n' }
        val lines = text.split("\n")
        if (lineIndex >= lines.size) return

        val lineStart = lines.take(lineIndex).sumOf { it.length + 1 }
        val currentLine = lines[lineIndex]

        if (lineIndex in bulletLines) {
            // Remove bullet
            bulletLines.remove(lineIndex)
            val newText = if (currentLine.startsWith("•  ")) {
                text.substring(0, lineStart) +
                    currentLine.removePrefix("•  ") +
                    text.substring(lineStart + currentLine.length)
            } else {
                text
            }
            val prefixLen = if (currentLine.startsWith("•  ")) 3 else 0
            if (prefixLen > 0) {
                adjustRangesForDeletion(lineStart, prefixLen)
            }
            val newCursor = (cursorPos - prefixLen).coerceIn(0, newText.length)

            isInternalUpdate = true
            textFieldValue = TextFieldValue(
                annotatedString = buildStyledText(newText),
                selection = TextRange(newCursor)
            )
            isInternalUpdate = false
        } else {
            // Add bullet
            bulletLines.add(lineIndex)
            val bulletPrefix = "•  "
            val newText = text.substring(0, lineStart) +
                bulletPrefix + currentLine +
                text.substring(lineStart + currentLine.length)
            adjustRangesForInsertion(lineStart, bulletPrefix.length)
            val newCursor = (cursorPos + bulletPrefix.length).coerceIn(0, newText.length)

            isInternalUpdate = true
            textFieldValue = TextFieldValue(
                annotatedString = buildStyledText(newText),
                selection = TextRange(newCursor)
            )
            isInternalUpdate = false
        }
    }

    /**
     * Check if a style is currently active.
     */
    fun isStyleActive(style: RichTextStyle): Boolean = style in activeStyles

    /**
     * Check if the current line is a bullet list item.
     */
    fun isBulletListActive(): Boolean {
        val text = textFieldValue.text
        val cursorPos = textFieldValue.selection.start.coerceIn(0, text.length)
        val lineIndex = text.substring(0, cursorPos).count { it == '\n' }
        return lineIndex in bulletLines
    }

    // ═══════════════════════════════ Private helpers ═══════════════════════════════

    /**
     * Build an AnnotatedString by applying our style ranges to plain text.
     */
    private fun buildStyledText(text: String): AnnotatedString {
        return buildAnnotatedString {
            append(text)
            for (range in styleRanges) {
                val s = range.start.coerceIn(0, text.length)
                val e = range.end.coerceIn(0, text.length)
                if (s < e) {
                    addStyle(range.style.spanStyle, s, e)
                    addStringAnnotation(range.style.tag, "", s, e)
                }
            }
        }
    }

    /**
     * Find where text was inserted by comparing old and new text.
     */
    private fun findInsertionPoint(oldText: String, newText: String, selection: TextRange): Int {
        // The cursor is typically right after the insertion
        val cursorPos = selection.start
        val insertLen = newText.length - oldText.length
        return (cursorPos - insertLen).coerceIn(0, oldText.length)
    }

    /**
     * Find where text was deleted by comparing old and new text.
     */
    private fun findDeletionPoint(oldText: String, newText: String, selection: TextRange): Int {
        // Find the first position where old and new text differ
        val cursorPos = selection.start
        return cursorPos.coerceIn(0, newText.length)
    }

    /**
     * Shift style ranges to account for inserted text.
     */
    private fun adjustRangesForInsertion(insertPos: Int, insertLen: Int) {
        val adjusted = styleRanges.map { range ->
            when {
                // Range is entirely before insertion — no change
                range.end <= insertPos -> range
                // Range is entirely after insertion — shift right
                range.start >= insertPos -> range.copy(
                    start = range.start + insertLen,
                    end = range.end + insertLen
                )
                // Insertion is inside the range — expand end
                else -> range.copy(end = range.end + insertLen)
            }
        }
        styleRanges.clear()
        styleRanges.addAll(adjusted)
    }

    /**
     * Shrink/remove style ranges to account for deleted text.
     */
    private fun adjustRangesForDeletion(deletePos: Int, deleteLen: Int) {
        val deleteEnd = deletePos + deleteLen
        val adjusted = styleRanges.mapNotNull { range ->
            when {
                // Range is entirely before deletion — no change
                range.end <= deletePos -> range
                // Range is entirely after deletion — shift left
                range.start >= deleteEnd -> range.copy(
                    start = range.start - deleteLen,
                    end = range.end - deleteLen
                )
                // Range is entirely within deletion — remove
                range.start >= deletePos && range.end <= deleteEnd -> null
                // Deletion overlaps start of range
                range.start < deletePos && range.end <= deleteEnd -> {
                    range.copy(end = deletePos)
                }
                // Deletion overlaps end of range
                range.start >= deletePos && range.end > deleteEnd -> {
                    range.copy(start = deletePos, end = range.end - deleteLen)
                }
                // Deletion is inside the range — shrink
                range.start < deletePos && range.end > deleteEnd -> {
                    range.copy(end = range.end - deleteLen)
                }
                else -> range
            }
        }.filter { it.start < it.end }
        styleRanges.clear()
        styleRanges.addAll(adjusted)
    }

    /**
     * Adjust bullet line indices when text is inserted.
     */
    private fun adjustBulletLinesForInsertion(
        oldText: String, newText: String,
        insertPos: Int, insertLen: Int
    ) {
        // Count how many newlines were in the inserted text
        val insertedText = newText.substring(insertPos, insertPos + insertLen)
        val newlineCount = insertedText.count { it == '\n' }
        if (newlineCount == 0) return

        // Find which line the insertion happened on
        val insertLineIndex = oldText.substring(0, insertPos.coerceIn(0, oldText.length))
            .count { it == '\n' }

        // Shift bullet lines that are after the insertion line
        val adjusted = bulletLines.map { lineIdx ->
            if (lineIdx > insertLineIndex) lineIdx + newlineCount else lineIdx
        }
        bulletLines.clear()
        bulletLines.addAll(adjusted)
    }

    /**
     * Adjust bullet line indices when text is deleted.
     */
    private fun adjustBulletLinesForDeletion(
        oldText: String, newText: String,
        deletePos: Int, deleteLen: Int
    ) {
        val deletedText = oldText.substring(deletePos, (deletePos + deleteLen).coerceIn(0, oldText.length))
        val newlineCount = deletedText.count { it == '\n' }
        if (newlineCount == 0) return

        val deleteLineIndex = oldText.substring(0, deletePos.coerceIn(0, oldText.length))
            .count { it == '\n' }

        val adjusted = bulletLines.mapNotNull { lineIdx ->
            when {
                lineIdx <= deleteLineIndex -> lineIdx
                lineIdx > deleteLineIndex + newlineCount -> lineIdx - newlineCount
                else -> null // Line was deleted
            }
        }
        bulletLines.clear()
        bulletLines.addAll(adjusted)
    }

    /**
     * Check if all text in [start, end) has the given style.
     */
    private fun hasStyleInRange(style: RichTextStyle, start: Int, end: Int): Boolean {
        val matching = styleRanges.filter { it.style == style }
        // Check if the matching ranges fully cover [start, end)
        if (matching.isEmpty()) return false

        val sorted = matching.sortedBy { it.start }
        var covered = start
        for (range in sorted) {
            if (range.start > covered) return false
            if (range.end > covered) covered = range.end
            if (covered >= end) return true
        }
        return covered >= end
    }

    /**
     * Remove a style from the range [start, end), splitting existing ranges if needed.
     */
    private fun removeStyleFromRange(style: RichTextStyle, start: Int, end: Int) {
        val toRemove = mutableListOf<StyleRange>()
        val toAdd = mutableListOf<StyleRange>()

        for (range in styleRanges) {
            if (range.style != style) continue
            if (range.end <= start || range.start >= end) continue // No overlap

            toRemove.add(range)

            // Keep portion before the removal range
            if (range.start < start) {
                toAdd.add(range.copy(end = start))
            }
            // Keep portion after the removal range
            if (range.end > end) {
                toAdd.add(range.copy(start = end))
            }
        }

        styleRanges.removeAll(toRemove)
        styleRanges.addAll(toAdd)
    }

    /**
     * Merge overlapping ranges of the same style.
     */
    private fun mergeOverlappingRanges() {
        for (style in RichTextStyle.entries) {
            val matching = styleRanges.filter { it.style == style }.sortedBy { it.start }
            if (matching.size <= 1) continue

            val merged = mutableListOf<StyleRange>()
            var current = matching[0]

            for (i in 1 until matching.size) {
                val next = matching[i]
                if (next.start <= current.end) {
                    // Overlapping or adjacent — merge
                    current = current.copy(end = maxOf(current.end, next.end))
                } else {
                    merged.add(current)
                    current = next
                }
            }
            merged.add(current)

            // Replace in styleRanges
            styleRanges.removeAll { it.style == style }
            styleRanges.addAll(merged)
        }
    }

    /**
     * Update active styles based on cursor position.
     */
    private fun updateActiveStylesFromCursor() {
        val selection = textFieldValue.selection
        if (!selection.collapsed) return // Don't update when there's a selection

        val pos = selection.start
        if (pos <= 0) {
            // At the beginning, keep activeStyles as-is for user intent
            return
        }

        val styles = mutableSetOf<RichTextStyle>()
        for (range in styleRanges) {
            // Cursor is inside this range
            if (pos > range.start && pos <= range.end) {
                styles.add(range.style)
            }
        }
        activeStyles = styles
    }

    /**
     * Build markdown for a single line's content by checking style ranges.
     */
    private fun buildLineWithMarkdown(content: String, globalOffset: Int): String {
        if (content.isEmpty()) return ""

        // Collect all style events within this line
        data class StyledSegment(val start: Int, val end: Int, val styles: Set<RichTextStyle>)

        // Build events at each boundary
        val boundaries = mutableSetOf(0, content.length)
        for (range in styleRanges) {
            val relStart = (range.start - globalOffset).coerceIn(0, content.length)
            val relEnd = (range.end - globalOffset).coerceIn(0, content.length)
            if (relStart < relEnd) {
                boundaries.add(relStart)
                boundaries.add(relEnd)
            }
        }

        val sortedBoundaries = boundaries.sorted()
        val segments = mutableListOf<StyledSegment>()

        for (i in 0 until sortedBoundaries.size - 1) {
            val segStart = sortedBoundaries[i]
            val segEnd = sortedBoundaries[i + 1]
            if (segStart >= segEnd) continue

            val styles = mutableSetOf<RichTextStyle>()
            for (range in styleRanges) {
                val relStart = (range.start - globalOffset).coerceIn(0, content.length)
                val relEnd = (range.end - globalOffset).coerceIn(0, content.length)
                if (relStart <= segStart && relEnd >= segEnd) {
                    styles.add(range.style)
                }
            }
            segments.add(StyledSegment(segStart, segEnd, styles))
        }

        val result = StringBuilder()
        for (seg in segments) {
            val text = content.substring(seg.start, seg.end)
            val hasBold = RichTextStyle.Bold in seg.styles
            val hasItalic = RichTextStyle.Italic in seg.styles
            val hasUnderline = RichTextStyle.Underline in seg.styles

            when {
                hasBold && hasItalic -> result.append("***").append(text).append("***")
                hasBold -> result.append("**").append(text).append("**")
                hasItalic -> result.append("*").append(text).append("*")
                hasUnderline -> result.append("__").append(text).append("__")
                else -> result.append(text)
            }
        }

        return result.toString()
    }
}
