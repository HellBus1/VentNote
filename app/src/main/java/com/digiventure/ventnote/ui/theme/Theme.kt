package com.digiventure.ventnote.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.digiventure.ventnote.commons.ColorPalletName
import com.digiventure.ventnote.commons.ColorSchemeName
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.data.local.NoteDataStore
import com.digiventure.ventnote.ui.ColorSchemeChoice
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@Composable
fun VentNoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val dataStore = NoteDataStore(LocalContext.current)

    val scope = rememberCoroutineScope()

    fun setColorPallet(colorPallet: String) {
        scope.launch {
            dataStore.setStringData(Constants.COLOR_PALLET, colorPallet)
        }
    }

    fun setColorScheme(colorScheme: String) {
        scope.launch {
            dataStore.setStringData(Constants.COLOR_SCHEME, colorScheme)
        }
    }

    val colorScheme = remember {
        val scheme = if (darkTheme) ColorSchemeName.DARK_MODE else
            ColorSchemeName.LIGHT_MODE
        mutableStateOf(
            ColorSchemeChoice.getColorScheme(
                scheme,
                ColorPalletName.PURPLE
            )
        )
    }

    LaunchedEffect(Unit) {
        val colorSchemeFlow = dataStore.getStringData(Constants.COLOR_SCHEME)
        val colorPalletFlow = dataStore.getStringData(Constants.COLOR_PALLET)

        val combinedFlow = combine(
            colorSchemeFlow, colorPalletFlow
        ) { scheme, pallet ->
            val defaultScheme =
                scheme.ifEmpty {
                    if (darkTheme) ColorSchemeName.DARK_MODE
                    else ColorSchemeName.LIGHT_MODE
                }
            val defaultPallet = pallet.ifEmpty { ColorPalletName.PURPLE }
            setColorScheme(defaultScheme)
            setColorPallet(defaultPallet)

            Pair(defaultScheme, defaultPallet)
        }

        combinedFlow.collect {
            colorScheme.value = ColorSchemeChoice.getColorScheme(
                it.first,
                it.second
            )
        }
    }

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        darkIcons = !darkTheme, color = colorScheme.value.primary
    )

    MaterialTheme(
        colorScheme = colorScheme.value, typography = Typography, content = content
    )
}