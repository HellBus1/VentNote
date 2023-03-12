package com.digiventure.ventnote.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingDialog(
    isOpened: Boolean,
    onDismissCallback: () -> Unit,
) {
    if (isOpened) {
        AlertDialog(
            onDismissRequest = { onDismissCallback() },
            content = {
                Surface(shape = RoundedCornerShape(8.dp)) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(24.dp),
                            strokeWidth = 2.dp,
                        )
                        Text(text = "Loading", fontSize = 16.sp, fontWeight = FontWeight.Normal)
                    }
                }
            }
        )
    }
}