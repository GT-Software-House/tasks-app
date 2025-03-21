package org.gabrielsantana.quicknotes.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.gabrielsantana.quicknotes.feature.preferences.appearance.data.PreferencesRepository
import org.gabrielsantana.quicknotes.feature.preferences.appearance.data.model.ThemeMode
import org.koin.compose.koinInject

@Stable
class AppState(
    preferencesRepository: PreferencesRepository,
    supabaseClient: SupabaseClient,
    coroutineScope: CoroutineScope,
) {
    val themeMode: StateFlow<ThemeMode> =
        preferencesRepository.getThemeMode().stateIn(coroutineScope, SharingStarted.WhileSubscribed(), ThemeMode.System)

    val isDynamicColorEnabled: StateFlow<Boolean> = preferencesRepository.getIsDynamicColorsEnabled()
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), false)

    val isAmoled: StateFlow<Boolean> = preferencesRepository.getIsAmoledColorsEnabled()
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), false)

    val seedColor: StateFlow<Color?> = preferencesRepository.getSeedColor().map { Color(it) }
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), null)


    val isLoggedIn: StateFlow<Boolean> = supabaseClient.auth.sessionStatus
        .map { it is SessionStatus.Authenticated }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = supabaseClient.auth.sessionStatus.value is SessionStatus.Authenticated
        )
}

@Composable
fun rememberAppState(
    preferencesRepository: PreferencesRepository = koinInject(),
    supabaseClient: SupabaseClient = koinInject(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): AppState = remember {
    AppState(preferencesRepository, supabaseClient, coroutineScope)
}

val State<ThemeMode>.isDarkMode: Boolean
    @Composable
    get() {
        return if (value is ThemeMode.System) isSystemInDarkTheme() else value is ThemeMode.Dark
    }