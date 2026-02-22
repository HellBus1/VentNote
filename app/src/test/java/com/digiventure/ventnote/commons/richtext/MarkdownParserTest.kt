package com.digiventure.ventnote.commons.richtext

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarkdownParserTest {

    @Test
    fun `plain text roundtrip preserves content`() {
        val input = "Hello world"
        val annotated = MarkdownParser.parseToAnnotatedString(input)
        assertEquals("Hello world", annotated.text)

        val markdown = MarkdownParser.toMarkdown(annotated)
        assertEquals(input, markdown)
    }

    @Test
    fun `empty string returns empty`() {
        val annotated = MarkdownParser.parseToAnnotatedString("")
        assertEquals("", annotated.text)

        val markdown = MarkdownParser.toMarkdown(annotated)
        assertEquals("", markdown)
    }

    @Test
    fun `bold text is parsed correctly`() {
        val input = "Hello **world**"
        val annotated = MarkdownParser.parseToAnnotatedString(input)
        assertEquals("Hello world", annotated.text)

        // Check that "world" has bold span style
        val spanStyles = annotated.spanStyles
        assertTrue(spanStyles.any {
            it.item.fontWeight == FontWeight.Bold &&
            it.start == 6 && it.end == 11
        })
    }

    @Test
    fun `italic text is parsed correctly`() {
        val input = "Hello *world*"
        val annotated = MarkdownParser.parseToAnnotatedString(input)
        assertEquals("Hello world", annotated.text)

        val spanStyles = annotated.spanStyles
        assertTrue(spanStyles.any {
            it.item.fontStyle == FontStyle.Italic &&
            it.start == 6 && it.end == 11
        })
    }

    @Test
    fun `underline text is parsed correctly`() {
        val input = "Hello __world__"
        val annotated = MarkdownParser.parseToAnnotatedString(input)
        assertEquals("Hello world", annotated.text)

        val spanStyles = annotated.spanStyles
        assertTrue(spanStyles.any {
            it.item.textDecoration == TextDecoration.Underline &&
            it.start == 6 && it.end == 11
        })
    }

    @Test
    fun `bold italic text is parsed correctly`() {
        val input = "Hello ***world***"
        val annotated = MarkdownParser.parseToAnnotatedString(input)
        assertEquals("Hello world", annotated.text)

        val spanStyles = annotated.spanStyles
        assertTrue(spanStyles.any {
            it.item.fontWeight == FontWeight.Bold &&
            it.start == 6 && it.end == 11
        })
        assertTrue(spanStyles.any {
            it.item.fontStyle == FontStyle.Italic &&
            it.start == 6 && it.end == 11
        })
    }

    @Test
    fun `bullet list is parsed correctly`() {
        val input = "- Buy groceries\n- Clean house"
        val annotated = MarkdownParser.parseToAnnotatedString(input)

        // Bullet prefix should be replaced with bullet character
        assertTrue(annotated.text.contains("•"))
        assertTrue(annotated.text.contains("Buy groceries"))
        assertTrue(annotated.text.contains("Clean house"))

        // Check bullet annotations exist
        val bulletAnnotations = annotated.getStringAnnotations(
            MarkdownParser.BULLET_TAG, 0, annotated.length
        )
        assertEquals(2, bulletAnnotations.size)
    }

    @Test
    fun `mixed content with bullet and formatting`() {
        val input = "- **Bold item**\n- *Italic item*"
        val annotated = MarkdownParser.parseToAnnotatedString(input)

        assertTrue(annotated.text.contains("Bold item"))
        assertTrue(annotated.text.contains("Italic item"))

        // Should have bold styling
        val spanStyles = annotated.spanStyles
        assertTrue(spanStyles.any { it.item.fontWeight == FontWeight.Bold })
        assertTrue(spanStyles.any { it.item.fontStyle == FontStyle.Italic })
    }

    @Test
    fun `multiline text preserves newlines`() {
        val input = "Line one\nLine two\nLine three"
        val annotated = MarkdownParser.parseToAnnotatedString(input)
        assertEquals("Line one\nLine two\nLine three", annotated.text)
    }

    @Test
    fun `bold text roundtrip`() {
        val input = "Hello **bold** world"
        val annotated = MarkdownParser.parseToAnnotatedString(input)
        val markdown = MarkdownParser.toMarkdown(annotated)
        assertEquals(input, markdown)
    }

    @Test
    fun `italic text roundtrip`() {
        val input = "Hello *italic* world"
        val annotated = MarkdownParser.parseToAnnotatedString(input)
        val markdown = MarkdownParser.toMarkdown(annotated)
        assertEquals(input, markdown)
    }

    @Test
    fun `underline text roundtrip`() {
        val input = "Hello __underline__ world"
        val annotated = MarkdownParser.parseToAnnotatedString(input)
        val markdown = MarkdownParser.toMarkdown(annotated)
        assertEquals(input, markdown)
    }

    @Test
    fun `unclosed markers treated as plain text`() {
        val input = "Hello **world"
        val annotated = MarkdownParser.parseToAnnotatedString(input)
        // Unclosed markers should be treated as plain text
        assertTrue(annotated.text.contains("*"))
    }

    @Test
    fun `text without formatting has no spans`() {
        val input = "Just plain text here"
        val annotated = MarkdownParser.parseToAnnotatedString(input)
        assertEquals("Just plain text here", annotated.text)
        assertTrue(annotated.spanStyles.isEmpty())
    }
}
