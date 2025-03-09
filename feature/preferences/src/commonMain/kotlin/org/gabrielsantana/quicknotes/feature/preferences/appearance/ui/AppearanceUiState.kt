package org.gabrielsantana.quicknotes.feature.preferences.appearance.ui

import org.gabrielsantana.quicknotes.feature.preferences.appearance.data.model.ThemeMode

data class AppearanceUiState(
    val themeMode: ThemeMode = ThemeMode.System,
    val isDynamicColorsEnabled: Boolean = false,
    val selectedSeedColor: Int? = null,
    val isAmoled: Boolean = false
)