package com.digiventure.ventnote.feature.note_creation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.digiventure.ventnote.data.persistence.NoteModel

class NoteCreationPageMockVM : ViewModel(), NoteCreationPageBaseVM {
    override val loader: MutableLiveData<Boolean> = MutableLiveData(false)
    override val titleText: MutableState<String> = mutableStateOf("")

    // Main text field state - matches the real implementation
    override val textFieldValue = mutableStateOf(TextFieldValue(""))

    // Computed property that stays in sync with textFieldValue
    override val descriptionText: MutableState<String> = mutableStateOf("").apply {
        // Keep this in sync with textFieldValue for compatibility
        // In preview, we'll manually update this when needed
    }

    // Formatting states - matches real implementation
    val isBoldActive = mutableStateOf(false)
    val isItalicActive = mutableStateOf(false)

    // Mock repository operation
    override suspend fun addNote(note: NoteModel): Result<Boolean> {
        return Result.success(true)
    }

    override fun loadFromMarkdown(markdown: String) {
        textFieldValue.value = TextFieldValue(
            text = markdown,
            selection = TextRange(markdown.length)
        )
        // Update descriptionText for compatibility
        descriptionText.value = markdown
        resetFormatToggleStates()
    }

    override fun updateFormatTogglesBasedOnCursor() {
        val currentTFV = textFieldValue.value
        if (currentTFV.selection.collapsed) {
            val cursorPos = currentTFV.selection.start
            val context = getMarkdownContextAtPosition(currentTFV.text, cursorPos)
            isBoldActive.value = context.isBold
            isItalicActive.value = context.isItalic
        } else {
            val selectionContext = getSelectionMarkdownContext(currentTFV)
            isBoldActive.value = selectionContext.isBold
            isItalicActive.value = selectionContext.isItalic
        }
    }

    override fun toggleBold() {
        when {
            textFieldValue.value.selection.collapsed -> toggleBoldAtCursor()
            else -> toggleBoldOnSelection()
        }
        // Update descriptionText for compatibility
        descriptionText.value = textFieldValue.value.text
    }

    override fun toggleItalic() {
        when {
            textFieldValue.value.selection.collapsed -> toggleItalicAtCursor()
            else -> toggleItalicOnSelection()
        }
        // Update descriptionText for compatibility
        descriptionText.value = textFieldValue.value.text
    }

    // --- Private Helper Methods (Simplified for Mock) ---
    private fun toggleBoldAtCursor() {
        val shouldActivate = !isBoldActive.value
        if (shouldActivate) {
            insertMarkdownAtCursor("**", "**")
        }
        isBoldActive.value = shouldActivate
    }

    private fun toggleBoldOnSelection() {
        val currentTFV = textFieldValue.value
        val context = getSelectionMarkdownContext(currentTFV)

        textFieldValue.value = when {
            context.isBold -> removeMarkdownFromSelection(currentTFV, "**")
            else -> addMarkdownToSelection(currentTFV, "**")
        }
    }

    private fun toggleItalicAtCursor() {
        val shouldActivate = !isItalicActive.value
        if (shouldActivate) {
            insertMarkdownAtCursor("*", "*")
        }
        isItalicActive.value = shouldActivate
    }

    private fun toggleItalicOnSelection() {
        val currentTFV = textFieldValue.value
        val context = getSelectionMarkdownContext(currentTFV)

        textFieldValue.value = when {
            context.isItalic -> removeMarkdownFromSelection(currentTFV, "*")
            context.isBold -> addMarkdownToSelection(removeMarkdownFromSelection(currentTFV, "**"), "***")
            else -> addMarkdownToSelection(currentTFV, "*")
        }
    }

    private fun insertMarkdownAtCursor(prefix: String, suffix: String) {
        val currentTFV = textFieldValue.value
        val cursorPos = currentTFV.selection.start

        val newText = currentTFV.text.substring(0, cursorPos) +
                prefix + suffix +
                currentTFV.text.substring(cursorPos)

        textFieldValue.value = TextFieldValue(
            text = newText,
            selection = TextRange(cursorPos + prefix.length)
        )
    }

    private fun addMarkdownToSelection(tfv: TextFieldValue, markdown: String): TextFieldValue {
        val selection = tfv.selection
        val selectedText = tfv.text.substring(selection.start, selection.end)

        val newText = tfv.text.substring(0, selection.start) +
                markdown + selectedText + markdown +
                tfv.text.substring(selection.end)

        return TextFieldValue(
            text = newText,
            selection = TextRange(
                selection.start + markdown.length,
                selection.end + markdown.length
            )
        )
    }

    private fun removeMarkdownFromSelection(tfv: TextFieldValue, markdown: String): TextFieldValue {
        val selection = tfv.selection
        val text = tfv.text

        val expandedStart = (selection.start - markdown.length).coerceAtLeast(0)
        val expandedEnd = (selection.end + markdown.length).coerceAtMost(text.length)

        val beforeMarker = text.substring(expandedStart, selection.start)
        val afterMarker = text.substring(selection.end, expandedEnd)

        return if (beforeMarker == markdown && afterMarker == markdown) {
            val newText = text.substring(0, expandedStart) +
                    text.substring(selection.start, selection.end) +
                    text.substring(expandedEnd)

            TextFieldValue(
                text = newText,
                selection = TextRange(
                    expandedStart,
                    selection.end - markdown.length
                )
            )
        } else {
            tfv
        }
    }

    private fun getMarkdownContextAtPosition(text: String, position: Int): MarkdownContext {
        if (position == 0) return MarkdownContext()

        val beforeCursor = text.substring(0, position)
        val afterCursor = text.substring(position)

        // Check for bold italic (***text***)
        if (beforeCursor.endsWith("***") && afterCursor.startsWith("***")) {
            return MarkdownContext(isBold = true, isItalic = true, isBoldItalic = true)
        }

        // Check for bold (**text**)
        if (beforeCursor.endsWith("**") && afterCursor.startsWith("**")) {
            return MarkdownContext(isBold = true)
        }

        // Check for italic (*text*)
        if (beforeCursor.endsWith("*") && afterCursor.startsWith("*") &&
            !beforeCursor.endsWith("**") && !afterCursor.startsWith("**")) {
            return MarkdownContext(isItalic = true)
        }

        return MarkdownContext()
    }

    private fun getSelectionMarkdownContext(tfv: TextFieldValue): MarkdownContext {
        val selection = tfv.selection
        val text = tfv.text

        if (selection.collapsed) {
            return getMarkdownContextAtPosition(text, selection.start)
        }

        val beforeStart = text.substring(0, selection.start)
        val afterEnd = text.substring(selection.end)

        // Check for bold italic
        if (beforeStart.endsWith("***") && afterEnd.startsWith("***")) {
            return MarkdownContext(isBold = true, isItalic = true, isBoldItalic = true)
        }

        // Check for bold
        if (beforeStart.endsWith("**") && afterEnd.startsWith("**")) {
            return MarkdownContext(isBold = true)
        }

        // Check for italic
        if (beforeStart.endsWith("*") && afterEnd.startsWith("*") &&
            !beforeStart.endsWith("**") && !afterEnd.startsWith("**")) {
            return MarkdownContext(isItalic = true)
        }

        return MarkdownContext()
    }

    private fun resetFormatToggleStates() {
        isBoldActive.value = false
        isItalicActive.value = false
    }

    // Data class for markdown context
    private data class MarkdownContext(
        val isBold: Boolean = false,
        val isItalic: Boolean = false,
        val isBoldItalic: Boolean = false
    )
}