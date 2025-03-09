package org.gabrielsantana.quicknotes.feature.preferences.appearance.data.model

sealed class ThemeMode {
    data object System : ThemeMode()
    data object Light : ThemeMode()
    data object Dark : ThemeMode()
}