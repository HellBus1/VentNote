package com.digiventure.ventnote.components.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextDialog(
    isOpened: Boolean,
    onDismissCallback: () -> Unit,
    onConfirmCallback: () -> Unit,
    title: String = "Warning",
    description: String = "Are you sure want to delete these items (it cannot be recovered)?",
) {
    if (isOpened) {
        AlertDialog(
            onDismissRequest = { onDismissCallback() },
            title = {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            },
            text = {
                Text(text = description, fontSize = 16.sp)
            },
            confirmButton = {
                TextButton(
                    onClick = { onConfirmCallback() },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Confirm", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismissCallback() },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Dismiss", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(8.dp)
        )
    }
}