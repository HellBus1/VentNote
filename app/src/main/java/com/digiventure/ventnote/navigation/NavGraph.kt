package com.digiventure.ventnote.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.digiventure.ventnote.feature.notes.NotesPage

@Composable
fun NavGraph(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = Route.NotesPage.routeName
    ) {
        composable(Route.NotesPage.routeName) {
            NotesPage(navHostController = navHostController)
        }
    }
}