package org.gabrielsantana.quicknotes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.gabrielsantana.quicknotes.di.appModule
import org.gabrielsantana.quicknotes.di.desktopModule
import org.gabrielsantana.quicknotes.ui.App
import org.koin.core.context.startKoin

@OptIn(ExperimentalFoundationApi::class)
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