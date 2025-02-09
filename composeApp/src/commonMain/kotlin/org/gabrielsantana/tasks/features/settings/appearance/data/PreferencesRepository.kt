@file:OptIn(ExperimentalSettingsApi::class)

package org.gabrielsantana.tasks.features.settings.appearance.data

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toBlockingSettings
import com.russhwolf.settings.coroutines.toSuspendSettings
import com.russhwolf.settings.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.gabrielsantana.tasks.ui.ThemeMode

class PreferencesRepository(
    private val flowSettings: FlowSettings,
) {

    @OptIn(ExperimentalSettingsApi::class)
    fun getThemeMode(): Flow<ThemeMode> {
        return flowSettings.getIntFlow(PreferencesKeys.THEME_MODE, 0).map { it.mapThemeMode() }
    }

    @OptIn(ExperimentalSettingsApi::class)
    suspend fun updateThemeMode(themeMode: ThemeMode) {
        flowSettings.putInt(PreferencesKeys.THEME_MODE, themeMode.mapInt())
    }

    @OptIn(ExperimentalSettingsApi::class)
    suspend fun updateDynamicColorPreference(useDynamicColor: Boolean) {
        flowSettings.putBoolean(PreferencesKeys.USE_DYNAMIC_COLOR, useDynamicColor)
    }
    @OptIn(ExperimentalSettingsApi::class)
    fun getDynamicColorPreference(): Flow<Boolean> {
        return flowSettings.getBooleanFlow(PreferencesKeys.USE_DYNAMIC_COLOR, false)
    }

    @OptIn(ExperimentalSettingsApi::class)
    suspend fun updateIsAmoled(isAmoled: Boolean) {
        flowSettings.putBoolean(PreferencesKeys.IS_AMOLED, isAmoled)
    }
    @OptIn(ExperimentalSettingsApi::class)
    fun getIsAmoled(): Flow<Boolean> {
        return flowSettings.getBooleanFlow(PreferencesKeys.IS_AMOLED, false)
    }

    @OptIn(ExperimentalSettingsApi::class)
    suspend fun updateSeedColor(color: Int) {
        flowSettings.putInt(PreferencesKeys.SEED_COLOR, color)
    }
    @OptIn(ExperimentalSettingsApi::class)
    fun getSeedColor(): Flow<Int?> {
        return flowSettings.getIntOrNullFlow(PreferencesKeys.SEED_COLOR)
    }
}

private object PreferencesKeys {
    const val THEME_MODE = "theme_mode"
    const val USE_DYNAMIC_COLOR = "use_dynamic_color"
    const val IS_AMOLED = "is_amoled"
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