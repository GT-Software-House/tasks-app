package org.gabrielsantana.tasks.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.gabrielsantana.tasks.features.settings.ColorType

@Composable
actual fun TasksTheme(
    darkTheme: Boolean,
    // Dynamic color is available on Android 12+
    isDynamicColorEnabled: Boolean,
    dynamicColorType: ColorType,
    content: @Composable () -> Unit
) {
    //TODO dynamic colors is only available for android, study remove the parameter for common code and find a way to use it only on android modules
    CommonTasksTheme(darkTheme, isDynamicColorEnabled, dynamicColorType, content)
}