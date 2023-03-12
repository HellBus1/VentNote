package com.digiventure.ventnote.feature.notes.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.digiventure.ventnote.navigation.Route

@Composable
fun NavDrawer(navHostController: NavHostController, drawerState: DrawerState, content: @Composable () -> Unit) {
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
                    icon = { Icon(Icons.Filled.AccountCircle, contentDescription = null, modifier = Modifier.size(24.dp)) },
                    label = { Text("About", fontSize = 16.sp) },
                    selected = false,
                    onClick = {
                        navHostController.navigate(Route.AboutPage.routeName)
                    },
                    shape = RectangleShape,
                    modifier = Modifier.padding(0.dp),
                )
            }
        },
        content = { content() }
    )
}