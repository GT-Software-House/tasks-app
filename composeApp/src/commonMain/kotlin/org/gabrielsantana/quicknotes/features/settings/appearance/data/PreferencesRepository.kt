@file:OptIn(ExperimentalSettingsApi::class)

package org.gabrielsantana.quicknotes.features.settings.appearance.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.gabrielsantana.quicknotes.ui.ThemeMode

class PreferencesRepository(
    private val flowSettings: FlowSettings,
) {

    @OptIn(ExperimentalSettingsApi::class)
    fun getThemeMode(): Flow<ThemeMode> {
        return flowSettings.getIntFlow(PreferencesKeys.THEME_MODE, DefaultPreferences.THEME_MODE)
            .map { it.mapThemeMode() }
    }

    @OptIn(ExperimentalSettingsApi::class)
    suspend fun updateThemeMode(themeMode: ThemeMode) {
        flowSettings.putInt(PreferencesKeys.THEME_MODE, themeMode.mapInt())
    }

    @OptIn(ExperimentalSettingsApi::class)
    suspend fun updateIsDynamicColors(isEnabled: Boolean) {
        flowSettings.putBoolean(PreferencesKeys.IS_DYNAMIC_COLORS_ENABLED, isEnabled)
    }

    @OptIn(ExperimentalSettingsApi::class)
    fun getIsDynamicColorsEnabled(): Flow<Boolean> {
        return flowSettings.getBooleanFlow(
            PreferencesKeys.IS_DYNAMIC_COLORS_ENABLED,
            defaultValue = DefaultPreferences.IS_DYNAMIC_COLORS_ENABLED
        )
    }

    @OptIn(ExperimentalSettingsApi::class)
    suspend fun updateIsAmoledColors(isEnabled: Boolean) {
        flowSettings.putBoolean(PreferencesKeys.IS_AMOLED_ENABLED, isEnabled)
    }

    @OptIn(ExperimentalSettingsApi::class)
    fun getIsAmoledColorsEnabled(): Flow<Boolean> {
        return flowSettings.getBooleanFlow(
            PreferencesKeys.IS_AMOLED_ENABLED,
            defaultValue = DefaultPreferences.IS_AMOLED_ENABLED
        )
    }

    @OptIn(ExperimentalSettingsApi::class)
    suspend fun updateSeedColor(color: Int) {
        flowSettings.putInt(PreferencesKeys.SEED_COLOR, color)
    }

    @OptIn(ExperimentalSettingsApi::class)
    fun getSeedColor(): Flow<Int> {
        return flowSettings.getIntFlow(PreferencesKeys.SEED_COLOR, DefaultPreferences.SEED_COLOR)
    }


}

private object DefaultPreferences {
    const val THEME_MODE = 2
    const val IS_DYNAMIC_COLORS_ENABLED = false
    const val IS_AMOLED_ENABLED = false
    val SEED_COLOR = Color.Blue.toArgb()
}

private object PreferencesKeys {
    const val THEME_MODE = "theme_mode"
    const val IS_DYNAMIC_COLORS_ENABLED = "use_dynamic_color"
    const val IS_AMOLED_ENABLED = "is_amoled"
    const val SEED_COLOR = "seed_color"
}

private fun ThemeMode.mapInt(): Int = when (this) {
    ThemeMode.Dark -> 0
    ThemeMode.Light -> 1
    ThemeMode.System -> 2
}

private fun Int.mapThemeMode() = when (this) {
    0 -> ThemeMode.Dark
    1 -> ThemeMode.Light
    else -> ThemeMode.System
}