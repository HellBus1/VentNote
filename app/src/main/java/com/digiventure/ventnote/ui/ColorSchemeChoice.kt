package com.digiventure.ventnote.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import com.digiventure.ventnote.commons.ColorPalletName
import com.digiventure.ventnote.commons.ColorSchemeName
import com.digiventure.ventnote.ui.theme.components.CadmiumGreenDarkPrimary
import com.digiventure.ventnote.ui.theme.components.CadmiumGreenDarkPrimaryContainer
import com.digiventure.ventnote.ui.theme.components.CadmiumGreenDarkSecondary
import com.digiventure.ventnote.ui.theme.components.CadmiumGreenDarkSecondaryContainer
import com.digiventure.ventnote.ui.theme.components.CadmiumGreenDarkTertiary
import com.digiventure.ventnote.ui.theme.components.CadmiumGreenDarkTertiaryContainer
import com.digiventure.ventnote.ui.theme.components.CadmiumGreenLightPrimary
import com.digiventure.ventnote.ui.theme.components.CadmiumGreenLightPrimaryContainer
import com.digiventure.ventnote.ui.theme.components.CadmiumGreenLightSecondary
import com.digiventure.ventnote.ui.theme.components.CadmiumGreenLightSecondaryContainer
import com.digiventure.ventnote.ui.theme.components.CadmiumGreenLightTertiary
import com.digiventure.ventnote.ui.theme.components.CadmiumGreenLightTertiaryContainer
import com.digiventure.ventnote.ui.theme.components.CobaltBlueDarkPrimary
import com.digiventure.ventnote.ui.theme.components.CobaltBlueDarkPrimaryContainer
import com.digiventure.ventnote.ui.theme.components.CobaltBlueDarkSecondary
import com.digiventure.ventnote.ui.theme.components.CobaltBlueDarkSecondaryContainer
import com.digiventure.ventnote.ui.theme.components.CobaltBlueDarkTertiary
import com.digiventure.ventnote.ui.theme.components.CobaltBlueDarkTertiaryContainer
import com.digiventure.ventnote.ui.theme.components.CobaltBlueLightPrimary
import com.digiventure.ventnote.ui.theme.components.CobaltBlueLightPrimaryContainer
import com.digiventure.ventnote.ui.theme.components.CobaltBlueLightSecondary
import com.digiventure.ventnote.ui.theme.components.CobaltBlueLightSecondaryContainer
import com.digiventure.ventnote.ui.theme.components.CobaltBlueLightTertiary
import com.digiventure.ventnote.ui.theme.components.CobaltBlueLightTertiaryContainer
import com.digiventure.ventnote.ui.theme.components.CrimsonDarkPrimary
import com.digiventure.ventnote.ui.theme.components.CrimsonDarkPrimaryContainer
import com.digiventure.ventnote.ui.theme.components.CrimsonDarkSecondary
import com.digiventure.ventnote.ui.theme.components.CrimsonDarkSecondaryContainer
import com.digiventure.ventnote.ui.theme.components.CrimsonDarkTertiary
import com.digiventure.ventnote.ui.theme.components.CrimsonDarkTertiaryContainer
import com.digiventure.ventnote.ui.theme.components.CrimsonLightPrimary
import com.digiventure.ventnote.ui.theme.components.CrimsonLightPrimaryContainer
import com.digiventure.ventnote.ui.theme.components.CrimsonLightSecondary
import com.digiventure.ventnote.ui.theme.components.CrimsonLightSecondaryContainer
import com.digiventure.ventnote.ui.theme.components.CrimsonLightTertiary
import com.digiventure.ventnote.ui.theme.components.CrimsonLightTertiaryContainer
import com.digiventure.ventnote.ui.theme.components.DarkBackground
import com.digiventure.ventnote.ui.theme.components.DarkOnBackground
import com.digiventure.ventnote.ui.theme.components.DarkOnPrimary
import com.digiventure.ventnote.ui.theme.components.DarkOnPrimaryContainer
import com.digiventure.ventnote.ui.theme.components.DarkOnSecondary
import com.digiventure.ventnote.ui.theme.components.DarkOnSecondaryContainer
import com.digiventure.ventnote.ui.theme.components.DarkOnSurface
import com.digiventure.ventnote.ui.theme.components.DarkOnSurfaceVariant
import com.digiventure.ventnote.ui.theme.components.DarkOnTertiary
import com.digiventure.ventnote.ui.theme.components.DarkOnTertiaryContainer
import com.digiventure.ventnote.ui.theme.components.DarkOutline
import com.digiventure.ventnote.ui.theme.components.DarkOutlineVariant
import com.digiventure.ventnote.ui.theme.components.DarkSurface
import com.digiventure.ventnote.ui.theme.components.DarkSurfaceVariant
import com.digiventure.ventnote.ui.theme.components.LightBackground
import com.digiventure.ventnote.ui.theme.components.LightOnBackground
import com.digiventure.ventnote.ui.theme.components.LightOnPrimary
import com.digiventure.ventnote.ui.theme.components.LightOnPrimaryContainer
import com.digiventure.ventnote.ui.theme.components.LightOnSecondary
import com.digiventure.ventnote.ui.theme.components.LightOnSecondaryContainer
import com.digiventure.ventnote.ui.theme.components.LightOnSurface
import com.digiventure.ventnote.ui.theme.components.LightOnSurfaceVariant
import com.digiventure.ventnote.ui.theme.components.LightOnTertiary
import com.digiventure.ventnote.ui.theme.components.LightOnTertiaryContainer
import com.digiventure.ventnote.ui.theme.components.LightOutline
import com.digiventure.ventnote.ui.theme.components.LightOutlineVariant
import com.digiventure.ventnote.ui.theme.components.LightSurface
import com.digiventure.ventnote.ui.theme.components.LightSurfaceVariant
import com.digiventure.ventnote.ui.theme.components.PurpleDarkPrimary
import com.digiventure.ventnote.ui.theme.components.PurpleDarkPrimaryContainer
import com.digiventure.ventnote.ui.theme.components.PurpleDarkSecondary
import com.digiventure.ventnote.ui.theme.components.PurpleDarkSecondaryContainer
import com.digiventure.ventnote.ui.theme.components.PurpleDarkTertiary
import com.digiventure.ventnote.ui.theme.components.PurpleDarkTertiaryContainer
import com.digiventure.ventnote.ui.theme.components.PurpleLightPrimary
import com.digiventure.ventnote.ui.theme.components.PurpleLightPrimaryContainer
import com.digiventure.ventnote.ui.theme.components.PurpleLightSecondary
import com.digiventure.ventnote.ui.theme.components.PurpleLightSecondaryContainer
import com.digiventure.ventnote.ui.theme.components.PurpleLightTertiary
import com.digiventure.ventnote.ui.theme.components.PurpleLightTertiaryContainer

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