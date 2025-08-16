package com.digiventure.ventnote.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import com.digiventure.ventnote.commons.ColorPalletName
import com.digiventure.ventnote.commons.ColorSchemeName
import com.digiventure.ventnote.ui.theme.CadmiumGreenDarkPrimary
import com.digiventure.ventnote.ui.theme.CadmiumGreenDarkPrimaryContainer
import com.digiventure.ventnote.ui.theme.CadmiumGreenDarkSecondary
import com.digiventure.ventnote.ui.theme.CadmiumGreenDarkSecondaryContainer
import com.digiventure.ventnote.ui.theme.CadmiumGreenDarkTertiary
import com.digiventure.ventnote.ui.theme.CadmiumGreenDarkTertiaryContainer
import com.digiventure.ventnote.ui.theme.CadmiumGreenLightPrimary
import com.digiventure.ventnote.ui.theme.CadmiumGreenLightPrimaryContainer
import com.digiventure.ventnote.ui.theme.CadmiumGreenLightSecondary
import com.digiventure.ventnote.ui.theme.CadmiumGreenLightSecondaryContainer
import com.digiventure.ventnote.ui.theme.CadmiumGreenLightTertiary
import com.digiventure.ventnote.ui.theme.CadmiumGreenLightTertiaryContainer
import com.digiventure.ventnote.ui.theme.CobaltBlueDarkPrimary
import com.digiventure.ventnote.ui.theme.CobaltBlueDarkPrimaryContainer
import com.digiventure.ventnote.ui.theme.CobaltBlueDarkSecondary
import com.digiventure.ventnote.ui.theme.CobaltBlueDarkSecondaryContainer
import com.digiventure.ventnote.ui.theme.CobaltBlueDarkTertiary
import com.digiventure.ventnote.ui.theme.CobaltBlueDarkTertiaryContainer
import com.digiventure.ventnote.ui.theme.CobaltBlueLightPrimary
import com.digiventure.ventnote.ui.theme.CobaltBlueLightPrimaryContainer
import com.digiventure.ventnote.ui.theme.CobaltBlueLightSecondary
import com.digiventure.ventnote.ui.theme.CobaltBlueLightSecondaryContainer
import com.digiventure.ventnote.ui.theme.CobaltBlueLightTertiary
import com.digiventure.ventnote.ui.theme.CobaltBlueLightTertiaryContainer
import com.digiventure.ventnote.ui.theme.CrimsonDarkPrimary
import com.digiventure.ventnote.ui.theme.CrimsonDarkPrimaryContainer
import com.digiventure.ventnote.ui.theme.CrimsonDarkSecondary
import com.digiventure.ventnote.ui.theme.CrimsonDarkSecondaryContainer
import com.digiventure.ventnote.ui.theme.CrimsonDarkTertiary
import com.digiventure.ventnote.ui.theme.CrimsonDarkTertiaryContainer
import com.digiventure.ventnote.ui.theme.CrimsonLightPrimary
import com.digiventure.ventnote.ui.theme.CrimsonLightPrimaryContainer
import com.digiventure.ventnote.ui.theme.CrimsonLightSecondary
import com.digiventure.ventnote.ui.theme.CrimsonLightSecondaryContainer
import com.digiventure.ventnote.ui.theme.CrimsonLightTertiary
import com.digiventure.ventnote.ui.theme.CrimsonLightTertiaryContainer
import com.digiventure.ventnote.ui.theme.DarkBackground
import com.digiventure.ventnote.ui.theme.DarkOnBackground
import com.digiventure.ventnote.ui.theme.DarkOnPrimary
import com.digiventure.ventnote.ui.theme.DarkOnPrimaryContainer
import com.digiventure.ventnote.ui.theme.DarkOnSecondary
import com.digiventure.ventnote.ui.theme.DarkOnSecondaryContainer
import com.digiventure.ventnote.ui.theme.DarkOnSurface
import com.digiventure.ventnote.ui.theme.DarkOnSurfaceVariant
import com.digiventure.ventnote.ui.theme.DarkOnTertiary
import com.digiventure.ventnote.ui.theme.DarkOnTertiaryContainer
import com.digiventure.ventnote.ui.theme.DarkOutline
import com.digiventure.ventnote.ui.theme.DarkOutlineVariant
import com.digiventure.ventnote.ui.theme.DarkSurface
import com.digiventure.ventnote.ui.theme.DarkSurfaceVariant
import com.digiventure.ventnote.ui.theme.LightBackground
import com.digiventure.ventnote.ui.theme.LightOnBackground
import com.digiventure.ventnote.ui.theme.LightOnPrimary
import com.digiventure.ventnote.ui.theme.LightOnPrimaryContainer
import com.digiventure.ventnote.ui.theme.LightOnSecondary
import com.digiventure.ventnote.ui.theme.LightOnSecondaryContainer
import com.digiventure.ventnote.ui.theme.LightOnSurface
import com.digiventure.ventnote.ui.theme.LightOnSurfaceVariant
import com.digiventure.ventnote.ui.theme.LightOnTertiary
import com.digiventure.ventnote.ui.theme.LightOnTertiaryContainer
import com.digiventure.ventnote.ui.theme.LightOutline
import com.digiventure.ventnote.ui.theme.LightOutlineVariant
import com.digiventure.ventnote.ui.theme.LightSurface
import com.digiventure.ventnote.ui.theme.LightSurfaceVariant
import com.digiventure.ventnote.ui.theme.PurpleDarkPrimary
import com.digiventure.ventnote.ui.theme.PurpleDarkPrimaryContainer
import com.digiventure.ventnote.ui.theme.PurpleDarkSecondary
import com.digiventure.ventnote.ui.theme.PurpleDarkSecondaryContainer
import com.digiventure.ventnote.ui.theme.PurpleDarkTertiary
import com.digiventure.ventnote.ui.theme.PurpleDarkTertiaryContainer
import com.digiventure.ventnote.ui.theme.PurpleLightPrimary
import com.digiventure.ventnote.ui.theme.PurpleLightPrimaryContainer
import com.digiventure.ventnote.ui.theme.PurpleLightSecondary
import com.digiventure.ventnote.ui.theme.PurpleLightSecondaryContainer
import com.digiventure.ventnote.ui.theme.PurpleLightTertiary
import com.digiventure.ventnote.ui.theme.PurpleLightTertiaryContainer

object ColorSchemeChoice {
    fun getColorScheme(colorScheme: String, colorPallet: String): ColorScheme {
        return when (colorScheme) {
            ColorSchemeName.DARK_MODE -> {
                when (colorPallet) {
                    ColorPalletName.CRIMSON -> DarkCrimsonScheme
                    ColorPalletName.CADMIUM_GREEN -> DarkCadmiumGreenScheme
                    ColorPalletName.COBALT_BLUE -> DarkCobaltBlueScheme
                    else -> DarkPurpleScheme
                }
            }
            else -> {
                when (colorPallet) {
                    ColorPalletName.CRIMSON -> LightCrimsonScheme
                    ColorPalletName.CADMIUM_GREEN -> LightCadmiumGreenScheme
                    ColorPalletName.COBALT_BLUE -> LightCobaltBlueScheme
                    else -> LightPurpleScheme
                }
            }
        }
    }

    private val DarkPurpleScheme = darkColorScheme(
        primary = PurpleDarkPrimary,
        onPrimary = DarkOnPrimary,
        primaryContainer = PurpleDarkPrimaryContainer,
        onPrimaryContainer = DarkOnPrimaryContainer,
        secondary = PurpleDarkSecondary,
        onSecondary = DarkOnSecondary,
        secondaryContainer = PurpleDarkSecondaryContainer,
        onSecondaryContainer = DarkOnSecondaryContainer,
        tertiary = PurpleDarkTertiary,
        onTertiary = DarkOnTertiary,
        tertiaryContainer = PurpleDarkTertiaryContainer,
        onTertiaryContainer = DarkOnTertiaryContainer,
        background = DarkBackground,
        onBackground = DarkOnBackground,
        surface = DarkSurface,
        onSurface = DarkOnSurface,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = DarkOnSurfaceVariant,
        outline = DarkOutline,
        outlineVariant = DarkOutlineVariant
    )

    private val LightPurpleScheme = lightColorScheme(
        primary = PurpleLightPrimary,
        onPrimary = LightOnPrimary,
        primaryContainer = PurpleLightPrimaryContainer,
        onPrimaryContainer = LightOnPrimaryContainer,
        secondary = PurpleLightSecondary,
        onSecondary = LightOnSecondary,
        secondaryContainer = PurpleLightSecondaryContainer,
        onSecondaryContainer = LightOnSecondaryContainer,
        tertiary = PurpleLightTertiary,
        onTertiary = LightOnTertiary,
        tertiaryContainer = PurpleLightTertiaryContainer,
        onTertiaryContainer = LightOnTertiaryContainer,
        background = LightBackground,
        onBackground = LightOnBackground,
        surface = LightSurface,
        onSurface = LightOnSurface,
        surfaceVariant = LightSurfaceVariant,
        onSurfaceVariant = LightOnSurfaceVariant,
        outline = LightOutline,
        outlineVariant = LightOutlineVariant
    )

    private val DarkCrimsonScheme = darkColorScheme(
        primary = CrimsonDarkPrimary,
        onPrimary = DarkOnPrimary,
        primaryContainer = CrimsonDarkPrimaryContainer,
        onPrimaryContainer = DarkOnPrimaryContainer,
        secondary = CrimsonDarkSecondary,
        onSecondary = DarkOnSecondary,
        secondaryContainer = CrimsonDarkSecondaryContainer,
        onSecondaryContainer = DarkOnSecondaryContainer,
        tertiary = CrimsonDarkTertiary,
        onTertiary = DarkOnTertiary,
        tertiaryContainer = CrimsonDarkTertiaryContainer,
        onTertiaryContainer = DarkOnTertiaryContainer,
        background = DarkBackground,
        onBackground = DarkOnBackground,
        surface = DarkSurface,
        onSurface = DarkOnSurface,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = DarkOnSurfaceVariant,
        outline = DarkOutline,
        outlineVariant = DarkOutlineVariant
    )

    private val LightCrimsonScheme = lightColorScheme(
        primary = CrimsonLightPrimary,
        onPrimary = LightOnPrimary,
        primaryContainer = CrimsonLightPrimaryContainer,
        onPrimaryContainer = LightOnPrimaryContainer,
        secondary = CrimsonLightSecondary,
        onSecondary = LightOnSecondary,
        secondaryContainer = CrimsonLightSecondaryContainer,
        onSecondaryContainer = LightOnSecondaryContainer,
        tertiary = CrimsonLightTertiary,
        onTertiary = LightOnTertiary,
        tertiaryContainer = CrimsonLightTertiaryContainer,
        onTertiaryContainer = LightOnTertiaryContainer,
        background = LightBackground,
        onBackground = LightOnBackground,
        surface = LightSurface,
        onSurface = LightOnSurface,
        surfaceVariant = LightSurfaceVariant,
        onSurfaceVariant = LightOnSurfaceVariant,
        outline = LightOutline,
        outlineVariant = LightOutlineVariant
    )

    private val DarkCadmiumGreenScheme = darkColorScheme(
        primary = CadmiumGreenDarkPrimary,
        onPrimary = DarkOnPrimary,
        primaryContainer = CadmiumGreenDarkPrimaryContainer,
        onPrimaryContainer = DarkOnPrimaryContainer,
        secondary = CadmiumGreenDarkSecondary,
        onSecondary = DarkOnSecondary,
        secondaryContainer = CadmiumGreenDarkSecondaryContainer,
        onSecondaryContainer = DarkOnSecondaryContainer,
        tertiary = CadmiumGreenDarkTertiary,
        onTertiary = DarkOnTertiary,
        tertiaryContainer = CadmiumGreenDarkTertiaryContainer,
        onTertiaryContainer = DarkOnTertiaryContainer,
        background = DarkBackground,
        onBackground = DarkOnBackground,
        surface = DarkSurface,
        onSurface = DarkOnSurface,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = DarkOnSurfaceVariant,
        outline = DarkOutline,
        outlineVariant = DarkOutlineVariant
    )

    private val LightCadmiumGreenScheme = lightColorScheme(
        primary = CadmiumGreenLightPrimary,
        onPrimary = LightOnPrimary,
        primaryContainer = CadmiumGreenLightPrimaryContainer,
        onPrimaryContainer = LightOnPrimaryContainer,
        secondary = CadmiumGreenLightSecondary,
        onSecondary = LightOnSecondary,
        secondaryContainer = CadmiumGreenLightSecondaryContainer,
        onSecondaryContainer = LightOnSecondaryContainer,
        tertiary = CadmiumGreenLightTertiary,
        onTertiary = LightOnTertiary,
        tertiaryContainer = CadmiumGreenLightTertiaryContainer,
        onTertiaryContainer = LightOnTertiaryContainer,
        background = LightBackground,
        onBackground = LightOnBackground,
        surface = LightSurface,
        onSurface = LightOnSurface,
        surfaceVariant = LightSurfaceVariant,
        onSurfaceVariant = LightOnSurfaceVariant,
        outline = LightOutline,
        outlineVariant = LightOutlineVariant
    )

    private val DarkCobaltBlueScheme = darkColorScheme(
        primary = CobaltBlueDarkPrimary,
        onPrimary = DarkOnPrimary,
        primaryContainer = CobaltBlueDarkPrimaryContainer,
        onPrimaryContainer = DarkOnPrimaryContainer,
        secondary = CobaltBlueDarkSecondary,
        onSecondary = DarkOnSecondary,
        secondaryContainer = CobaltBlueDarkSecondaryContainer,
        onSecondaryContainer = DarkOnSecondaryContainer,
        tertiary = CobaltBlueDarkTertiary,
        onTertiary = DarkOnTertiary,
        tertiaryContainer = CobaltBlueDarkTertiaryContainer,
        onTertiaryContainer = DarkOnTertiaryContainer,
        background = DarkBackground,
        onBackground = DarkOnBackground,
        surface = DarkSurface,
        onSurface = DarkOnSurface,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = DarkOnSurfaceVariant,
        outline = DarkOutline,
        outlineVariant = DarkOutlineVariant
    )

    private val LightCobaltBlueScheme = lightColorScheme(
        primary = CobaltBlueLightPrimary,
        onPrimary = LightOnPrimary,
        primaryContainer = CobaltBlueLightPrimaryContainer,
        onPrimaryContainer = LightOnPrimaryContainer,
        secondary = CobaltBlueLightSecondary,
        onSecondary = LightOnSecondary,
        secondaryContainer = CobaltBlueLightSecondaryContainer,
        onSecondaryContainer = LightOnSecondaryContainer,
        tertiary = CobaltBlueLightTertiary,
        onTertiary = LightOnTertiary,
        tertiaryContainer = CobaltBlueLightTertiaryContainer,
        onTertiaryContainer = LightOnTertiaryContainer,
        background = LightBackground,
        onBackground = LightOnBackground,
        surface = LightSurface,
        onSurface = LightOnSurface,
        surfaceVariant = LightSurfaceVariant,
        onSurfaceVariant = LightOnSurfaceVariant,
        outline = LightOutline,
        outlineVariant = LightOutlineVariant
    )
}