package com.digiventure.ventnote.ui.theme.viewmodel

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.digiventure.ventnote.commons.ColorPalletName
import com.digiventure.ventnote.commons.ColorSchemeName
import com.digiventure.ventnote.commons.Constants.EMPTY_STRING
import com.digiventure.ventnote.ui.ColorSchemeChoice

class ThemeMockVM: ViewModel(), ThemeBaseVM {
    override val currentColorScheme: MutableState<ColorScheme> = mutableStateOf(ColorSchemeChoice.getColorScheme(
        ColorSchemeName.DARK_MODE, ColorPalletName.PURPLE
    ))
    override val currentColorSchemeName: MutableState<String> = mutableStateOf(EMPTY_STRING)

    override fun updateColorPallet(colorPallet: String) {

    }

    override fun updateColorScheme(colorSchemeName: String) {

    }
}