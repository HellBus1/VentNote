package com.digiventure.ventnote.ui.theme.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.digiventure.ventnote.ui.theme.viewmodel.ThemeBaseVM
import com.digiventure.ventnote.ui.theme.viewmodel.ThemeVM
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun VentNoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeVM: ThemeBaseVM = hiltViewModel<ThemeVM>(),
    content: @Composable () -> Unit
) {
    val colorScheme by themeVM.currentColorScheme

    // Update system UI based on the theme from ViewModel
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(colorScheme, darkTheme) {
        systemUiController.setStatusBarColor(
            darkIcons = !darkTheme,
            color = colorScheme.primary
        )
        systemUiController.setSystemBarsColor(
            darkIcons = !darkTheme,
            color = colorScheme.primary
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
