package com.digiventure.ventnote.feature.notes.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun NavDrawer(
    navHostController: NavHostController,
    drawerState: DrawerState,
    content: @Composable () -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current

    fun openPlayStore(appURL: String) {
        val playIntent: Intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(appURL)
        }
        try {
            context.startActivity(playIntent)
        } catch (e: Exception) {
            onError("Cannot Open URL")
        }
    }

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
                    icon = { Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(24.dp)) },
                    label = { Text("Rate App", fontSize = 16.sp) },
                    selected = false,
                    onClick = {
                        openPlayStore("https://play.google.com/store/apps/details?id=com.digiventure.ventnote")
                    },
                    shape = RectangleShape,
                    modifier = Modifier.padding(0.dp),
                )
            }
        },
        content = { content() }
    )
}