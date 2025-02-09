package org.gabrielsantana.tasks.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.gabrielsantana.tasks.features.settings.ColorType
import org.gabrielsantana.tasks.features.settings.appearance.data.PreferencesRepository

@Stable
class AppState(
    private val preferencesRepository: PreferencesRepository
) {
    val themeMode: State<ThemeMode>
        @Composable
        get() = preferencesRepository.getThemeMode().collectAsStateWithLifecycle(ThemeMode.System)

    val isDynamicColorEnabled: State<Boolean>
        @Composable
        get() = preferencesRepository.getDynamicColorPreference().collectAsStateWithLifecycle(false)

    val isAmoled: State<Boolean>
        @Composable
        get() = preferencesRepository.getIsAmoled().collectAsStateWithLifecycle(false)

    val seedColor: State<Color?>
        @Composable
        get() = preferencesRepository
            .getSeedColor()
            .map { it?.let { Color(it) } }
            .collectAsStateWithLifecycle(null)


}

val AppState.isDarkMode: Boolean
    @Composable
    get() {
        return if (themeMode.value is ThemeMode.System) isSystemInDarkTheme() else themeMode.value is ThemeMode.Dark
    }

sealed class ThemeMode {
    data object System : ThemeMode()
    data object Light : ThemeMode()
    data object Dark : ThemeMode()
}


@Composable
fun rememberAppState(preferencesRepository: PreferencesRepository): AppState = remember {
    AppState(preferencesRepository)
}