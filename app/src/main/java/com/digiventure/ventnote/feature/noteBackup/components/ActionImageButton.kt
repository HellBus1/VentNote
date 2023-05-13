package com.digiventure.ventnote.feature.noteBackup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.digiventure.ventnote.ui.theme.Purple40
import com.digiventure.ventnote.ui.theme.PurpleGrey40

@Composable
fun ActionImageButton(
    imageVector: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        IconButton(
            onClick = { if (enabled) onClick() },
            modifier = Modifier.background(if (enabled) Purple40 else PurpleGrey40)
                .padding(10.dp)
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = "",
                tint = Color.White
            )
        }
    }
}