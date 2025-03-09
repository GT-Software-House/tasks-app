package org.gabrielsantana.quicknotes.features.settings.appearance.ui

import org.gabrielsantana.quicknotes.ui.ThemeMode

data class AppearanceUiState(
    val themeMode: ThemeMode = ThemeMode.System,
    val isDynamicColorsEnabled: Boolean = false,
    val selectedSeedColor: Int? = null,
    val isAmoled: Boolean = false
)