package com.digiventure.ventnote.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import com.digiventure.ventnote.commons.ColorPalletName
import com.digiventure.ventnote.commons.ColorSchemeName
import com.digiventure.ventnote.ui.theme.CrimsonDarkPrimary
import com.digiventure.ventnote.ui.theme.CrimsonDarkSecondary
import com.digiventure.ventnote.ui.theme.CrimsonLightPrimary
import com.digiventure.ventnote.ui.theme.CrimsonLightSecondary
import com.digiventure.ventnote.ui.theme.DarkBackground
import com.digiventure.ventnote.ui.theme.DarkOnSurface
import com.digiventure.ventnote.ui.theme.DarkTertiary
import com.digiventure.ventnote.ui.theme.LightBackground
import com.digiventure.ventnote.ui.theme.LightOnSurface
import com.digiventure.ventnote.ui.theme.LightTertiary
import com.digiventure.ventnote.ui.theme.PurpleDarkPrimary
import com.digiventure.ventnote.ui.theme.PurpleDarkSecondary
import com.digiventure.ventnote.ui.theme.PurpleLightPrimary
import com.digiventure.ventnote.ui.theme.PurpleLightSecondary

object ColorSchemeChoice {
    fun getColorScheme(colorScheme: String, colorPallet: String): ColorScheme {
        return when (colorScheme) {
            ColorSchemeName.DARK_MODE -> {
                when (colorPallet) {
                    ColorPalletName.CRIMSON -> DarkCrimsonScheme
                    else -> DarkPurpleScheme
                }
            }
            else -> {
                when (colorPallet) {
                    ColorPalletName.CRIMSON -> LightCrimsonScheme
                    else -> LightPurpleScheme
                }
            }
        }
    }

    private val DarkPurpleScheme = darkColorScheme(
        primary = PurpleDarkPrimary,
        secondary = PurpleDarkSecondary,
        tertiary = DarkTertiary,
        background = DarkBackground,
        surface = DarkTertiary,
        onPrimary = DarkTertiary,
        onSurface = DarkOnSurface
    )

    private val LightPurpleScheme = lightColorScheme(
        primary = PurpleLightPrimary,
        secondary = PurpleLightSecondary,
        tertiary = LightTertiary,
        background = LightBackground,
        surface = LightTertiary,
        onPrimary = LightTertiary,
        onSurface = LightOnSurface
    )

    private val DarkCrimsonScheme = darkColorScheme(
        primary = CrimsonDarkPrimary,
        secondary = CrimsonDarkSecondary,
        tertiary = DarkTertiary,
        background = DarkBackground,
        surface = DarkTertiary,
        onPrimary = DarkTertiary,
        onSurface = DarkOnSurface
    )

    private val LightCrimsonScheme = lightColorScheme(
        primary = CrimsonLightPrimary,
        secondary = CrimsonLightSecondary,
        tertiary = LightTertiary,
        background = LightBackground,
        surface = LightTertiary,
        onPrimary = LightTertiary,
        onSurface = LightOnSurface
    )
}