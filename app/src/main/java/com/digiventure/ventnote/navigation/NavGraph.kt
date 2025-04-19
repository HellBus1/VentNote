package com.digiventure.ventnote.navigation

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.feature.backup.BackupPage
import com.digiventure.ventnote.feature.note_creation.NoteCreationPage
import com.digiventure.ventnote.feature.note_detail.NoteDetailPage
import com.digiventure.ventnote.feature.notes.NotesPage
import com.digiventure.ventnote.feature.share_preview.SharePreviewPage

@Composable
fun NavGraph(navHostController: NavHostController, openDrawer: () -> Unit) {
    val emptyString = ""
    val noteIdNavArgument = "noteId"
    val noteDataNavArgument = "noteData"
    val stringZero = "0"

    NavHost(
        navController = navHostController,
        startDestination = Route.NotesPage.routeName,
    ) {
        composable(Route.NotesPage.routeName) {
            NotesPage(navHostController = navHostController, openDrawer = openDrawer)
        }
        composable(
            route = "${Route.NoteDetailPage.routeName}/{${noteIdNavArgument}}",
            arguments = listOf(navArgument(noteIdNavArgument) {
                type = NavType.StringType
                defaultValue = emptyString
            })
        ) {
            NoteDetailPage(navHostController = navHostController,
                id = it.arguments?.getString(noteIdNavArgument) ?: stringZero)
        }
        composable(Route.NoteCreationPage.routeName) {
            NoteCreationPage(navHostController = navHostController)
        }
        composable(
            route = "${Route.SharePreviewPage.routeName}/{${noteDataNavArgument}}",
            arguments = listOf(navArgument(noteDataNavArgument) {
                type = NoteModelParamType()
            })
        ) {
            val note = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.arguments?.getParcelable(noteDataNavArgument, NoteModel::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.arguments?.getParcelable(noteDataNavArgument)
            }
            SharePreviewPage(navHostController = navHostController, note = note)
        }
        composable(Route.BackupPage.routeName) {
            BackupPage(navHostController = navHostController)
        }
    }
}