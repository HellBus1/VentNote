package com.digiventure.ventnote.commons.richtext

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan

/**
 * Converts a markdown string to an Android SpannableString for use with
 * traditional Views (e.g. RemoteViews in widgets).
 *
 * This is the View-system equivalent of [MarkdownParser.parseToAnnotatedString],
 * which produces a Compose AnnotatedString. Both parse the same markdown format.
 */
object MarkdownToSpannable {

    /**
     * Convert a markdown string to a SpannableStringBuilder suitable for
     * RemoteViews.setTextViewText() and standard TextViews.
     */
    fun convert(markdown: String): SpannableStringBuilder {
        if (markdown.isEmpty()) return SpannableStringBuilder("")

        val result = SpannableStringBuilder()
        val lines = markdown.split("\n")

        lines.forEachIndexed { lineIndex, line ->
            val isBullet = line.startsWith("- ")
            val contentLine = if (isBullet) line.removePrefix("- ") else line

            if (isBullet) {
                result.append("•  ")
            }

            parseInlineStyles(result, contentLine)

            if (lineIndex < lines.lastIndex) {
                result.append("\n")
            }
        }

        return result
    }

    private fun parseInlineStyles(builder: SpannableStringBuilder, text: String) {
        var i = 0
        while (i < text.length) {
            when {
                // Bold+Italic: ***text***
                text.startsWith("***", i) -> {
                    val endIdx = text.indexOf("***", i + 3)
                    if (endIdx != -1) {
                        val content = text.substring(i + 3, endIdx)
                        val start = builder.length
                        builder.append(content)
                        builder.setSpan(
                            StyleSpan(Typeface.BOLD_ITALIC),
                            start, builder.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        i = endIdx + 3
                    } else {
                        builder.append(text[i])
                        i++
                    }
                }
                // Bold: **text**
                text.startsWith("**", i) -> {
                    val endIdx = text.indexOf("**", i + 2)
                    if (endIdx != -1) {
                        val content = text.substring(i + 2, endIdx)
                        val start = builder.length
                        builder.append(content)
                        builder.setSpan(
                            StyleSpan(Typeface.BOLD),
                            start, builder.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        i = endIdx + 2
                    } else {
                        builder.append(text[i])
                        i++
                    }
                }
                // Underline: __text__
                text.startsWith("__", i) -> {
                    val endIdx = text.indexOf("__", i + 2)
                    if (endIdx != -1) {
                        val content = text.substring(i + 2, endIdx)
                        val start = builder.length
                        builder.append(content)
                        builder.setSpan(
                            UnderlineSpan(),
                            start, builder.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        i = endIdx + 2
                    } else {
                        builder.append(text[i])
                        i++
                    }
                }
                // Italic: *text*
                text.startsWith("*", i) -> {
                    val endIdx = text.indexOf("*", i + 1)
                    if (endIdx != -1) {
                        val content = text.substring(i + 1, endIdx)
                        val start = builder.length
                        builder.append(content)
                        builder.setSpan(
                            StyleSpan(Typeface.ITALIC),
                            start, builder.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        i = endIdx + 1
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
}
