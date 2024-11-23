package com.digiventure.ventnote.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

class PageNavigation(navController: NavHostController) {
    val navigateToBackupPage: () -> Unit = {
        navController.navigate(Route.BackupPage.routeName) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToDetailPage: (noteId: Int) -> Unit = { noteId ->
        val routeName = "${Route.NoteDetailPage.routeName}/${noteId}"
        navController.navigate(routeName) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToCreatePage: () -> Unit = {
        navController.navigate(Route.NoteCreationPage.routeName) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToNotesPage: () -> Unit = {
        navController.navigate(Route.NotesPage.routeName) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
        }
    }
}