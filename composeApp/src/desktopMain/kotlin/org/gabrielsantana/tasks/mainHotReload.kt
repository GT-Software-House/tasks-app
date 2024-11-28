package org.gabrielsantana.tasks

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.LocalTextContextMenu
import androidx.compose.foundation.text.TextContextMenu
import androidx.compose.foundation.text.TextContextMenu.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.gabrielsantana.tasks.di.appModule
import org.gabrielsantana.tasks.di.desktopModule
import org.gabrielsantana.tasks.ui.App
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.koin.core.context.startKoin

@OptIn(ExperimentalFoundationApi::class)
fun main() {
    startKoin {
        modules(appModule + desktopModule)
    }
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Tasks - Development",
        ) {
            DevelopmentEntryPoint {
                App()
            }
        }
    }
}
