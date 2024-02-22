package com.digiventure.ventnote.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import com.digiventure.ventnote.commons.ColorPalletName
import com.digiventure.ventnote.commons.ColorSchemeName
import com.digiventure.ventnote.ui.theme.CrimsonDarkBackground
import com.digiventure.ventnote.ui.theme.CrimsonDarkOnSurface
import com.digiventure.ventnote.ui.theme.CrimsonDarkPrimary
import com.digiventure.ventnote.ui.theme.CrimsonDarkSecondary
import com.digiventure.ventnote.ui.theme.CrimsonDarkTertiary
import com.digiventure.ventnote.ui.theme.CrimsonLightBackground
import com.digiventure.ventnote.ui.theme.CrimsonLightOnSurface
import com.digiventure.ventnote.ui.theme.CrimsonLightPrimary
import com.digiventure.ventnote.ui.theme.CrimsonLightSecondary
import com.digiventure.ventnote.ui.theme.CrimsonLightTertiary
import com.digiventure.ventnote.ui.theme.PurpleLightOnSurface
import com.digiventure.ventnote.ui.theme.PurpleDarkTertiary
import com.digiventure.ventnote.ui.theme.PurpleLightPrimary
import com.digiventure.ventnote.ui.theme.PurpleDarkPrimary
import com.digiventure.ventnote.ui.theme.PurpleLightSecondary
import com.digiventure.ventnote.ui.theme.PurpleDarkSecondary
import com.digiventure.ventnote.ui.theme.PurpleDarkOnSurface
import com.digiventure.ventnote.ui.theme.PurpleDarkBackground
import com.digiventure.ventnote.ui.theme.PurpleLightTertiary
import com.digiventure.ventnote.ui.theme.PurpleLightBackground

object ColorSchemeChoice {
    fun getColorScheme(colorScheme: String, colorPallet: String): ColorScheme {

        return when (colorScheme) {
            ColorSchemeName.DARK_MODE -> {
                when (colorPallet) {
                    ColorPalletName.PURPLE -> DarkPurpleScheme
                    ColorPalletName.CRIMSON -> DarkCrimsonScheme
                    else -> throw IllegalArgumentException("Unsupported color palette")
                }
            }
            ColorSchemeName.LIGHT_MODE -> {
                when (colorPallet) {
                    ColorPalletName.PURPLE -> LightPurpleScheme
                    ColorPalletName.CRIMSON -> LightCrimsonScheme
                    else -> throw IllegalArgumentException("Unsupported color palette")
                }
            }
            else -> throw IllegalArgumentException("Unsupported color scheme")
        }
    }

    private val DarkPurpleScheme = darkColorScheme(
        primary = PurpleDarkPrimary,
        secondary = PurpleDarkSecondary,
        tertiary = PurpleDarkTertiary,
        background = PurpleDarkBackground,
        surface = PurpleDarkTertiary,
        onPrimary = PurpleDarkTertiary,
        onSurface = PurpleDarkOnSurface
    )

    private val LightPurpleScheme = lightColorScheme(
        primary = PurpleLightPrimary,
        secondary = PurpleLightSecondary,
        tertiary = PurpleLightTertiary,
        background = PurpleLightBackground,
        surface = PurpleLightTertiary,
        onPrimary = PurpleLightTertiary,
        onSurface = PurpleLightOnSurface
    )

    private val DarkCrimsonScheme = darkColorScheme(
        primary = CrimsonDarkPrimary,
        secondary = CrimsonDarkSecondary,
        tertiary = CrimsonDarkTertiary,
        background = CrimsonDarkBackground,
        surface = CrimsonDarkTertiary,
        onPrimary = CrimsonDarkTertiary,
        onSurface = CrimsonDarkOnSurface
    )

    private val LightCrimsonScheme = lightColorScheme(
        primary = CrimsonLightPrimary,
        secondary = CrimsonLightSecondary,
        tertiary = CrimsonLightTertiary,
        background = CrimsonLightBackground,
        surface = CrimsonLightTertiary,
        onPrimary = CrimsonLightTertiary,
        onSurface = CrimsonLightOnSurface
    )
}