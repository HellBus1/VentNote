package com.digiventure.ventnote.feature.noteBackup.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileImage() {
    Icon(
        imageVector = Icons.Filled.AccountCircle,
        contentDescription = "",
        modifier = Modifier.size(64.dp)
    )
}