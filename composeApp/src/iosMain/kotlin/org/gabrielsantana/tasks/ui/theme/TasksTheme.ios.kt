package org.gabrielsantana.tasks.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
actual fun TasksTheme(darkTheme: Boolean, dynamicColor: Boolean, content: @Composable () -> Unit) {
    //TODO dynamic colors is only available for android, study remove the parameter for common code and find a way to use it only on android modules
    CommonTasksTheme(darkTheme, content)
}