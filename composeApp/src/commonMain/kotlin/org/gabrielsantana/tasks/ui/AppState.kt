package org.gabrielsantana.tasks.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
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
import org.gabrielsantana.tasks.features.settings.appearance.data.PreferencesRepository
import org.koin.compose.koinInject

@Stable
class AppState(
    private val preferencesRepository: PreferencesRepository,
    private val supabaseClient: SupabaseClient,
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
            started = SharingStarted.WhileSubscribed(),
            initialValue = supabaseClient.auth.sessionStatus.value is SessionStatus.Authenticated
        )
}

val ThemeMode.isDarkMode: Boolean
    @Composable
    get() {
        return if (this is ThemeMode.System) isSystemInDarkTheme() else this is ThemeMode.Dark
    }

sealed class ThemeMode {
    data object System : ThemeMode()
    data object Light : ThemeMode()
    data object Dark : ThemeMode()
}


@Composable
fun rememberAppState(
    preferencesRepository: PreferencesRepository = koinInject(),
    supabaseClient: SupabaseClient = koinInject(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): AppState = remember {
    AppState(preferencesRepository, supabaseClient, coroutineScope)
}