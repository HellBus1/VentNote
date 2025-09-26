package com.digiventure.ventnote.feature.note_creation.components.section

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digiventure.ventnote.feature.note_creation.viewmodel.NoteCreationPageBaseVM
import com.digiventure.ventnote.feature.note_creation.viewmodel.NoteCreationPageMockVM
import com.digiventure.ventnote.feature.note_creation.viewmodel.NoteCreationPageVM
import com.digiventure.ventnote.helper.MarkdownVisualTransformation

@Composable
fun RichTextField(
    viewModel: NoteCreationPageBaseVM,
    bodyTextFieldSemantic: String,
    bodyInputPlaceholder: String
) {
    val borderColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.primary,
        animationSpec = tween(300),
        label = "border_color_rich_text"
    )

    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .heightIn(min = 200.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { focusRequester.requestFocus() }
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(width = 2.dp, color = borderColor)
    ) {
        // Cast to access textFieldValue - this is the key fix
        val actualVM = viewModel as? NoteCreationPageVM
        val textFieldValue by remember { actualVM?.textFieldValue ?: mutableStateOf(TextFieldValue("")) }

        TextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                actualVM?.textFieldValue?.value = newValue
            },
            textStyle = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            singleLine = false,
            visualTransformation = MarkdownVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxSize()
                .semantics { contentDescription = bodyTextFieldSemantic }
                .focusRequester(focusRequester),
            placeholder = {
                Text(
                    text = bodyInputPlaceholder,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    ),
                )
            }
        )
    }
}

@Composable
fun StyleControls(viewModel: NoteCreationPageBaseVM) {
    val actualVM = viewModel as? NoteCreationPageVM
    val isBoldActive by remember { actualVM?.isBoldActive ?: mutableStateOf(false) }
    val isItalicActive by remember { actualVM?.isItalicActive ?: mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bold Button
            IconButton(
                onClick = { viewModel.toggleBold() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FormatBold,
                    contentDescription = "Toggle Bold",
                    tint = if (isBoldActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Italic Button
            IconButton(
                onClick = { viewModel.toggleItalic() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FormatItalic,
                    contentDescription = "Toggle Italic",
                    tint = if (isItalicActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Status Text
            Text(
                text = when {
                    isBoldActive && isItalicActive -> "Bold + Italic"
                    isBoldActive -> "Bold"
                    isItalicActive -> "Italic"
                    else -> "Plain text"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteSectionMarkdownPreview() {
    MaterialTheme {
        val dummyViewModel = remember { NoteCreationPageMockVM() }

        // Initialize with some markdown
        LaunchedEffect(Unit) {
            dummyViewModel.loadFromMarkdown(
                "This is **bold** and *italic*.\n\n" +
                        "- Item 1\n" +
                        "- Item 2\n\n" +
                        "Another paragraph with ***bold and italic***."
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Rich Text Editor Preview",
                style = MaterialTheme.typography.headlineSmall
            )

            StyleControls(viewModel = dummyViewModel)

            RichTextField(
                viewModel = dummyViewModel,
                bodyTextFieldSemantic = "Note content field",
                bodyInputPlaceholder = "Enter your note here..."
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        println("Markdown Output: ${dummyViewModel.descriptionText.value}")
                    }
                ) {
                    Text("Log Markdown")
                }

                OutlinedButton(
                    onClick = {
                        dummyViewModel.loadFromMarkdown("**New bold text** and *italic text*")
                    }
                ) {
                    Text("Load Sample")
                }
            }

            // Show current text length
            Text(
                text = "Characters: ${dummyViewModel.textFieldValue.value.text.length}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}