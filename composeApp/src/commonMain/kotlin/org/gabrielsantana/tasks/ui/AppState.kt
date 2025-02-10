package org.gabrielsantana.tasks.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.gabrielsantana.tasks.features.settings.appearance.data.PreferencesRepository

@Stable
class AppState(
    private val preferencesRepository: PreferencesRepository,
    coroutineScope: CoroutineScope,
) {
    val themeMode: State<ThemeMode>
        @Composable
        get() = preferencesRepository.getThemeMode().collectAsStateWithLifecycle(ThemeMode.System)

    val isDynamicColorEnabled: State<Boolean>
        @Composable
        get() = preferencesRepository.getIsDynamicColorsEnabled().collectAsStateWithLifecycle(false)

    val isAmoled: State<Boolean>
        @Composable
        get() = preferencesRepository.getIsAmoledColorsEnabled().collectAsStateWithLifecycle(false)

    val seedColor: State<Color?>
        @Composable
        get() = preferencesRepository
            .getSeedColor()
            .map { it?.let { Color(it) } }
            .collectAsStateWithLifecycle(null)


    val isLoggedIn = callbackFlow {
        Firebase.auth.authStateChanged.collect { user ->
            trySend(user != null)
        }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(), Firebase.auth.currentUser != null)
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
fun rememberAppState(
    preferencesRepository: PreferencesRepository,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): AppState = remember {
    AppState(preferencesRepository, coroutineScope)
}