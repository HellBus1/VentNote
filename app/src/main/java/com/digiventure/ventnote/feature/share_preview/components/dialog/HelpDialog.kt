package com.digiventure.ventnote.feature.share_preview.components.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.digiventure.ventnote.R

@Composable
fun HelpDialog(
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        icon = {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = stringResource(R.string.information),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = stringResource(R.string.share_note_information),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onDismissRequest() }
            ) {
                Text(stringResource(R.string.close))
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}