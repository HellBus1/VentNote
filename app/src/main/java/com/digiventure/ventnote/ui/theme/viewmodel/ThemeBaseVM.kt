package com.digiventure.ventnote.ui.theme.viewmodel

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.MutableState

interface ThemeBaseVM {
    /**
     *
     * */
    val currentColorScheme: MutableState<ColorScheme>

    /**
     *
     * */
    val currentColorSchemeName: MutableState<String>

    /**
     *
     * */
    fun updateColorPallet(colorPallet: String)

    /**
     *
     * */
    fun updateColorScheme(colorSchemeName: String)
}