package com.digiventure.ventnote.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomAlertDialog(
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