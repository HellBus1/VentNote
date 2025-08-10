package com.digiventure.ventnote.components.bottomSheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegularBottomSheet(
    isOpened: Boolean,
    bottomSheetState: SheetState,
    onDismissRequest: () -> Unit,
    modifier: Modifier?,
    content: @Composable () -> Unit
) {
    if (isOpened) {
        ModalBottomSheet(
            onDismissRequest = { onDismissRequest() },
            sheetState = bottomSheetState,
            modifier = modifier ?: Modifier,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            content()
        }
    }
}