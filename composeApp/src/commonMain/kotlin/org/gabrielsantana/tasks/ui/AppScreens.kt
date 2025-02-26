package org.gabrielsantana.tasks.ui

import kotlinx.serialization.Serializable

@Serializable
sealed class AppScreens {
    @Serializable
    data object Login : AppScreens()

    @Serializable
    data object Home : AppScreens()

    @Serializable
    data class CreateTask(val taskUuid: String? = null) : AppScreens()

    @Serializable
    data object Settings : AppScreens()

    @Serializable
    data object Appearance : AppScreens()
}