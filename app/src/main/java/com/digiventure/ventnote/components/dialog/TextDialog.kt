package com.digiventure.ventnote.components.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.TestTags

@Composable
fun TextDialog(
    modifier: Modifier = Modifier,
    isOpened: Boolean,
    onDismissCallback: () -> Unit,
    onConfirmCallback: (() -> Unit)? = null,
    title: String = stringResource(R.string.warning_title),
    description: String = stringResource(R.string.delete_confirmation_text),
) {
    if (isOpened) {
        AlertDialog(
            onDismissRequest = { onDismissCallback() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                if (onConfirmCallback != null) {
                    TextButton(
                        onClick = { onConfirmCallback() },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.semantics { testTag = TestTags.CONFIRM_BUTTON }
                    ) {
                        Text(
                            text = stringResource(R.string.confirm),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                            )
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismissCallback() },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.semantics { testTag = TestTags.DISMISS_BUTTON }
                ) {
                    Text(
                        text = stringResource(R.string.dismiss),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        )
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
        )
    }
}