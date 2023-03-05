package com.digiventure.ventnote.feature.notes.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun NavDrawer(drawerState: DrawerState, content: @Composable () -> Unit) {
    ModalNavigationDrawer(
        drawerState= drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RectangleShape,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp)
                    .padding(0.dp)
            ) {
                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                    label = { Text("Deleted") },
                    selected = false,
                    onClick = {

                    },
                    shape = RectangleShape,
                    modifier = Modifier.padding(0.dp),
                )

            }
        },
        content = { content() }
    )
}