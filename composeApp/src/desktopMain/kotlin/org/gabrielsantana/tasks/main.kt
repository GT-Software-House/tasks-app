package org.gabrielsantana.tasks

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.ContextMenuState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.LocalTextContextMenu
import androidx.compose.foundation.text.TextContextMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.gabrielsantana.tasks.di.appModule
import org.gabrielsantana.tasks.di.desktopModule
import org.gabrielsantana.tasks.ui.App
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