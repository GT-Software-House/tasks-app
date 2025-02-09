package org.gabrielsantana.tasks.features.settings.appearance.ui

import org.gabrielsantana.tasks.ui.ThemeMode

data class AppearanceUiState(
    val themeMode: ThemeMode = ThemeMode.System,
    val isDynamicColorsEnabled: Boolean = false,
    val selectedSeedColor: Int? = null,
    val isAmoled: Boolean = false
)