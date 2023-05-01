package com.digiventure.ventnote.components.bottomSheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegularBottomSheet(
    isOpened: Boolean,
    bottomSheetState: SheetState,
    onDismissRequest: () -> Unit,
    content: @Composable() () -> Unit
) {
    if (isOpened) {
        ModalBottomSheet(
            onDismissRequest = { onDismissRequest() },
            sheetState = bottomSheetState,
        ) {
            content()
        }
    }
}