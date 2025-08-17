package com.digiventure.ventnote.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

class PageNavigation(navController: NavHostController) {
    val navigateToBackupPage: () -> Unit = {
        navController.navigate(Route.BackupPage.routeName)
    }
    val navigateToDetailPage: (noteId: Int) -> Unit = { noteId ->
        val routeName = "${Route.NoteDetailPage.routeName}/${noteId}"
        navController.navigate(routeName)
    }
    val navigateToCreatePage: () -> Unit = {
        navController.navigate(Route.NoteCreationPage.routeName)
    }
    val navigateToNotesPage: () -> Unit = {
        navController.navigate(Route.NotesPage.routeName) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
                saveState = true
            }
        }
    }
    val navigateToSharePage: (noteJson: String) -> Unit = { noteJson ->
        val routeName = "${Route.SharePreviewPage.routeName}/${noteJson}"
        navController.navigate(routeName)
    }
}