package com.digiventure.ventnote.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.digiventure.ventnote.commons.ColorPalletName
import com.digiventure.ventnote.commons.ColorSchemeName
import com.digiventure.ventnote.ui.ColorSchemeChoice
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.data.NoteDataStore
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun VentNoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val dataStore = NoteDataStore(LocalContext.current)

    val colorSchemeName = remember { mutableStateOf(
        if (darkTheme) ColorSchemeName.DARK_MODE else ColorSchemeName.LIGHT_MODE
    ) }
    val colorPalletName = remember { mutableStateOf(ColorPalletName.PURPLE) }
    val colorScheme by remember(colorSchemeName, colorPalletName) {
        derivedStateOf {
            ColorSchemeChoice.getColorScheme(colorPalletName.value, colorSchemeName.value)
        }
    }

    LaunchedEffect(colorSchemeName.value, colorPalletName.value) {
        val colorSchemeFlow = dataStore.getStringData(Constants.COLOR_SCHEME)
        val colorPalletFlow = dataStore.getStringData(Constants.COLOR_PALLET)

        colorSchemeFlow.collect {
            colorSchemeName.value = it
        }

        colorPalletFlow.collect {
            colorPalletName.value = it
        }
    }

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        darkIcons = !darkTheme,
        color = colorScheme.surface
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}