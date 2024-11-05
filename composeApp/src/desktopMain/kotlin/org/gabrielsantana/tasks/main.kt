package org.gabrielsantana.tasks

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.gabrielsantana.tasks.di.appModule
import org.gabrielsantana.tasks.di.desktopModule
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(appModule + desktopModule)
    }
    application {

        Window(
            onCloseRequest = ::exitApplication,
            title = "Tasks",
        ) {
            App()
        }
    }
}