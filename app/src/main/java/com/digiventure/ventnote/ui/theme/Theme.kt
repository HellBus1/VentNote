package com.digiventure.ventnote.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = NobleBlack,

    background = Sooty,
    surface = NobleBlack,

    onPrimary = NobleBlack,
    onSurface = SilverMistral
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = White,

    background = WhiteSolid,
    surface = White,

    onPrimary = White,
    onSurface = BlackRibbon
)

@Composable
fun VentNoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        darkIcons = !darkTheme,
        color = when(darkTheme) {
            true -> DarkColorScheme.surface
            else -> LightColorScheme.surface
        }
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}