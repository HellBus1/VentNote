package com.digiventure.ventnote.feature.tag_manager.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.digiventure.ventnote.data.persistence.TagModel

private const val MAX_TAG_NAME_LENGTH = 20

/**
 * Dialog for creating or editing a tag.
 *
 * @param existingTag  When non-null, pre-fills the dialog fields for editing.
 * @param onConfirm    Called with the final [TagModel] when user confirms.
 * @param onDismiss    Called when user cancels or dismisses.
 */
@Composable
fun TagEditorDialog(
    existingTag: TagModel? = null,
    onConfirm: (TagModel) -> Unit,
    onDismiss: () -> Unit
) {
    val isEditing = existingTag != null

    var tagName by remember { mutableStateOf(existingTag?.name ?: "") }
    var selectedColor by remember { mutableStateOf(existingTag?.colorHex ?: TAG_COLOR_PALETTE.first().first) }

    val isConfirmEnabled = tagName.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = if (isEditing) "Edit Tag" else "New Tag",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Name field
                OutlinedTextField(
                    value = tagName,
                    onValueChange = { if (it.length <= MAX_TAG_NAME_LENGTH) tagName = it },
                    label = { Text("Tag name") },
                    placeholder = { Text("e.g. Work, Personal…") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    supportingText = { Text("${tagName.length}/$MAX_TAG_NAME_LENGTH") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Color picker
                TagColorPicker(
                    selectedHex = selectedColor,
                    onColorSelected = { selectedColor = it }
                )

                // Preview chip
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Preview",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                TagChip(
                    tag = TagModel(
                        id = existingTag?.id ?: 0,
                        name = tagName.ifBlank { "Preview" },
                        colorHex = selectedColor
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        TagModel(
                            id = existingTag?.id ?: 0,
                            name = tagName.trim(),
                            colorHex = selectedColor
                        )
                    )
                },
                enabled = isConfirmEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isEditing) "Save" else "Create", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
