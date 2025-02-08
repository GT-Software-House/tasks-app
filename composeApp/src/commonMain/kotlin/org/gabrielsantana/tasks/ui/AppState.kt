package org.gabrielsantana.tasks.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import org.gabrielsantana.tasks.features.settings.ColorType

@Stable
class AppState(
    coroutineScope: CoroutineScope,
) {
    var themeMode by mutableStateOf<ThemeMode>(ThemeMode.System)
    var isDynamicColorsEnabled by mutableStateOf(false)


    val isLoggedIn = callbackFlow {
        Firebase.auth.authStateChanged.collect { user ->
            trySend(user != null)
        }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(), Firebase.auth.currentUser != null)

}

val AppState.isDarkMode: Boolean
    @Composable
    get() {
        return if (themeMode is ThemeMode.System) isSystemInDarkTheme() else themeMode is ThemeMode.Dark
    }

sealed class ThemeMode {
    data object System : ThemeMode()
    data object Light : ThemeMode()
    data object Dark : ThemeMode()
}


@Composable
fun rememberAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): AppState = remember {
    AppState(coroutineScope)
}