package com.digiventure.ventnote.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.digiventure.ventnote.feature.about.AboutPage
import com.digiventure.ventnote.feature.help.HelpPage
import com.digiventure.ventnote.feature.noteCreation.NoteCreationPage
import com.digiventure.ventnote.feature.noteDetail.NoteDetailPage
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
        composable(
            route = "${Route.NoteDetailPage.routeName}/{noteId}",
            arguments = listOf(navArgument("noteId") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) {
            NoteDetailPage(navHostController = navHostController)
        }
        composable(Route.NoteCreationPage.routeName) {
            NoteCreationPage(navHostController = navHostController)
        }
        composable(Route.HelpPage.routeName) {
            HelpPage(navHostController = navHostController)
        }
        composable(Route.AboutPage.routeName) {
            AboutPage(navHostController = navHostController)
        }
    }
}