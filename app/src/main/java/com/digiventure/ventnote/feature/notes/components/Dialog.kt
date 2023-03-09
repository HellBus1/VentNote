package com.digiventure.ventnote.feature.notes.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun CustomAlertDialog(isOpened: Boolean, onDismissCallback: () -> Unit, onConfirmCallback: () -> Unit) {
    if (isOpened) {
        AlertDialog(
            onDismissRequest = { onDismissCallback() },
            title = {
                Text(text = "Warning")
            },
            text = {
                Text(text = "Are you sure to delete these items (it cannot recovered)?")
            },
            confirmButton = {
                TextButton(
                    onClick = { onConfirmCallback() }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismissCallback() }
                ) {
                    Text("Dismiss")
                }
            }

        )
    }
}