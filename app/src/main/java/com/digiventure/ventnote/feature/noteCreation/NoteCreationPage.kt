package com.digiventure.ventnote.feature.noteCreation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.digiventure.ventnote.feature.noteCreation.components.NoteCreationAppBar

@Composable
fun NoteCreationPage(navHostController: NavHostController) {
    Scaffold(
        topBar = {
            NoteCreationAppBar()
        },
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {

        }
    }
}