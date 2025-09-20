package com.digiventure.ventnote.ui.theme.viewmodel

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digiventure.ventnote.commons.ColorPalletName
import com.digiventure.ventnote.commons.ColorSchemeName
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.data.local.NoteDataStore
import com.digiventure.ventnote.ui.ColorSchemeChoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeVM @Inject constructor(
    private val dataStore: NoteDataStore,
) : ViewModel(), ThemeBaseVM {
    private val _currentColorScheme = mutableStateOf(getDefaultColorScheme())
    private val _currentColorSchemeName = mutableStateOf(getDefaultColorSchemeName())
    override val currentColorScheme: MutableState<ColorScheme> = _currentColorScheme
    override val currentColorSchemeName: MutableState<String> = _currentColorSchemeName

    // Flows for observing datastore changes
    private val colorSchemePreferenceFlow = dataStore.getStringData(Constants.COLOR_SCHEME)
    private val colorPalletPreferenceFlow = dataStore.getStringData(Constants.COLOR_PALLET)

    init {
        viewModelScope.launch {
            combine(
                colorSchemePreferenceFlow,
                colorPalletPreferenceFlow
            ) { schemePref, palletPref ->
                val currentSystemIsDark = false
                val effectiveSchemeName = schemePref.ifEmpty {
                    if (currentSystemIsDark) ColorSchemeName.DARK_MODE
                    else ColorSchemeName.LIGHT_MODE
                }
                val effectivePalletName = palletPref.ifEmpty { ColorPalletName.PURPLE }

                if (schemePref.isEmpty()) {
                    dataStore.setStringData(Constants.COLOR_SCHEME, effectiveSchemeName)
                }
                if (palletPref.isEmpty()) {
                    dataStore.setStringData(Constants.COLOR_PALLET, effectivePalletName)
                }

                ColorSchemeChoice.getColorScheme(effectiveSchemeName, effectivePalletName)
            }.collect { newScheme ->
                _currentColorScheme.value = newScheme
            }
        }
    }

    override fun updateColorPallet(colorPallet: String) {
        viewModelScope.launch {
            dataStore.setStringData(Constants.COLOR_PALLET, colorPallet)
        }
    }

    override fun updateColorScheme(colorSchemeName: String) {
        viewModelScope.launch {
            dataStore.setStringData(Constants.COLOR_SCHEME, colorSchemeName)
            _currentColorSchemeName.value = colorSchemeName
        }
    }

    private fun getDefaultColorScheme(isSystemDark: Boolean = false): ColorScheme {
        val schemeName = this.getDefaultColorSchemeName(isSystemDark);
        return ColorSchemeChoice.getColorScheme(schemeName, ColorPalletName.PURPLE)
    }

    private fun getDefaultColorSchemeName(isSystemDark: Boolean = false): String {
        val schemeName = if (isSystemDark) ColorSchemeName.DARK_MODE else ColorSchemeName.LIGHT_MODE
        return schemeName
    }
}