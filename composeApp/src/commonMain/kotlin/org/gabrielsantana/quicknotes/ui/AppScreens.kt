package org.gabrielsantana.quicknotes.ui

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppScreens {
    @Serializable
    data object Login : AppScreens

    @Serializable
    data object Home : AppScreens

    @Serializable
    data class CreateTask(val taskUuid: String? = null) : AppScreens

    @Serializable
    data object Preferences : AppScreens
}