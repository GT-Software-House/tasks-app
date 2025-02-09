package org.gabrielsantana.tasks.features.settings.appearance.ui

import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.gabrielsantana.tasks.features.settings.appearance.data.PreferencesRepository
import org.gabrielsantana.tasks.ui.ThemeMode

class AppearanceViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppearanceUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        combine(
            preferencesRepository.getIsDynamicColorsEnabled(),
            preferencesRepository.getThemeMode(),
            preferencesRepository.getIsAmoledColorsEnabled(),
            preferencesRepository.getSeedColor()
        ) { dynamicColor, themeMode, isAmoled, seedColor ->
            AppearanceUiState(
                isDynamicColorsEnabled = dynamicColor,
                themeMode = themeMode,
                selectedSeedColor = seedColor,
                isAmoled = isAmoled
            )
        }.onEach {
            _uiState.value = it
        }.launchIn(viewModelScope)
    }

    fun updateThemeModePreference(themeMode: ThemeMode) {
        viewModelScope.launch {
            preferencesRepository.updateThemeMode(themeMode)
        }
    }

    fun updateDynamicColorPreference(useDynamicColor: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateIsDynamicColors(useDynamicColor)
        }
    }

    fun updateSelectedColorPreference(@ColorInt color: Int) {
        viewModelScope.launch {
            preferencesRepository.updateSeedColor(color)
        }
    }

    fun updateAmoledPreference(isAmoled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateIsAmoledColors(isAmoled)
        }
    }
}