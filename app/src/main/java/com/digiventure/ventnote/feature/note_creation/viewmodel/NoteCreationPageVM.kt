package com.digiventure.ventnote.feature.note_creation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digiventure.ventnote.commons.Constants.EMPTY_STRING
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NoteCreationPageVM @Inject constructor(
    private val repository: NoteRepository
) : ViewModel(), NoteCreationPageBaseVM {

    override val loader: MutableLiveData<Boolean> = MutableLiveData(false)
    override val titleText: MutableState<String> = mutableStateOf(EMPTY_STRING)

    // Main text field state
    override val textFieldValue = mutableStateOf(TextFieldValue(EMPTY_STRING))

    // Computed property that stays in sync with textFieldValue
    override val descriptionText: MutableState<String> = derivedStateOf {
        textFieldValue.value.text
    }.let { derived ->
        mutableStateOf(derived.value).apply {
            snapshotFlow { textFieldValue.value.text }
                .onEach { value = it }
                .launchIn(viewModelScope)
        }
    }

    // Formatting states
    val isBoldActive = mutableStateOf(false)
    val isItalicActive = mutableStateOf(false)

    init {
        // Update formatting toggles when selection changes
        snapshotFlow { textFieldValue.value.selection }
            .onEach { updateFormatTogglesBasedOnCursor() }
            .launchIn(viewModelScope)
    }

    // --- Repository Operations ---
    override suspend fun addNote(note: NoteModel): Result<Boolean> = withContext(Dispatchers.IO) {
        loader.postValue(true)
        try {
            repository.insertNote(note).onEach {
                loader.postValue(false)
            }.last()
        } catch (e: Exception) {
            loader.postValue(false)
            Result.failure(e)
        }
    }

    override fun toggleBold() {
        val currentTFV = textFieldValue.value
        val selection = currentTFV.selection

        if (selection.collapsed) {
            // Cursor position - insert markdown markers
            val cursorPos = selection.start
            val context = getMarkdownContextAtPosition(currentTFV.text, cursorPos)

            if (!context.isBold) {
                insertMarkdownAtCursor("**", "**")
                isBoldActive.value = true
            }
        } else {
            // Text selection - apply/remove formatting
            textFieldValue.value = toggleMarkdownOnSelection(currentTFV, "**")
            updateFormatTogglesBasedOnCursor()
        }
    }

    override fun toggleItalic() {
        val currentTFV = textFieldValue.value
        val selection = currentTFV.selection

        if (selection.collapsed) {
            val cursorPos = selection.start
            val context = getMarkdownContextAtPosition(currentTFV.text, cursorPos)

            if (!context.isItalic) {
                insertMarkdownAtCursor("*", "*")
                isItalicActive.value = true
            }
        } else {
            textFieldValue.value = toggleMarkdownOnSelection(currentTFV, "*")
            updateFormatTogglesBasedOnCursor()
        }
    }

    override fun loadFromMarkdown(markdown: String) {
        textFieldValue.value = TextFieldValue(
            text = markdown,
            selection = TextRange(markdown.length)
        )
        resetFormatToggleStates()
    }

    override fun updateFormatTogglesBasedOnCursor() {
        val currentTFV = textFieldValue.value
        val cursorPos = currentTFV.selection.start
        val context = getMarkdownContextAtPosition(currentTFV.text, cursorPos)

        isBoldActive.value = context.isBold
        isItalicActive.value = context.isItalic
    }

    private fun insertMarkdownAtCursor(prefix: String, suffix: String) {
        val currentTFV = textFieldValue.value
        val cursorPos = currentTFV.selection.start

        val newText = StringBuilder(currentTFV.text).apply {
            insert(cursorPos, prefix + suffix)
        }.toString()

        textFieldValue.value = TextFieldValue(
            text = newText,
            selection = TextRange(cursorPos + prefix.length)
        )
    }

    private fun toggleMarkdownOnSelection(tfv: TextFieldValue, marker: String): TextFieldValue {
        val selection = tfv.selection
        val selectedText = tfv.text.substring(selection.start, selection.end)

        // Check if selection is already wrapped with this marker
        val beforeStart = maxOf(0, selection.start - marker.length)
        val afterEnd = minOf(tfv.text.length, selection.end + marker.length)

        val isAlreadyFormatted = selection.start >= marker.length &&
                selection.end <= tfv.text.length - marker.length &&
                tfv.text.substring(beforeStart, selection.start) == marker &&
                tfv.text.substring(selection.end, afterEnd) == marker

        return if (isAlreadyFormatted) {
            // Remove formatting
            val newText = tfv.text.substring(0, beforeStart) +
                    selectedText +
                    tfv.text.substring(afterEnd)

            TextFieldValue(
                text = newText,
                selection = TextRange(beforeStart, beforeStart + selectedText.length)
            )
        } else {
            // Add formatting
            val newText = tfv.text.substring(0, selection.start) +
                    marker + selectedText + marker +
                    tfv.text.substring(selection.end)

            TextFieldValue(
                text = newText,
                selection = TextRange(
                    selection.start + marker.length,
                    selection.end + marker.length
                )
            )
        }
    }

    private fun getMarkdownContextAtPosition(text: String, position: Int): MarkdownContext {
        if (position == 0 || text.isEmpty()) return MarkdownContext()

        // Simple context detection - look at surrounding characters
        val beforeCursor = text.substring(0, position)
        val afterCursor = text.substring(position)

        // Check for different markdown patterns
        val isBold = (beforeCursor.endsWith("**") && afterCursor.startsWith("**")) ||
                isInsidePattern(text, position, "**")

        val isItalic = (beforeCursor.endsWith("*") && afterCursor.startsWith("*") &&
                !beforeCursor.endsWith("**") && !afterCursor.startsWith("**")) ||
                isInsidePattern(text, position, "*")

        return MarkdownContext(isBold = isBold, isItalic = isItalic)
    }

    private fun isInsidePattern(text: String, position: Int, pattern: String): Boolean {
        val beforeText = text.substring(0, position)
        val afterText = text.substring(position)

        // Count pattern occurrences before and after cursor
        val beforeCount = beforeText.split(pattern).size - 1
        val afterCount = afterText.split(pattern).size - 1

        // If odd number before cursor, we're likely inside a pattern
        return beforeCount % 2 == 1 && afterCount > 0
    }

    private fun resetFormatToggleStates() {
        isBoldActive.value = false
        isItalicActive.value = false
    }

    private data class MarkdownContext(
        val isBold: Boolean = false,
        val isItalic: Boolean = false,
        val isBoldItalic: Boolean = false
    )
}