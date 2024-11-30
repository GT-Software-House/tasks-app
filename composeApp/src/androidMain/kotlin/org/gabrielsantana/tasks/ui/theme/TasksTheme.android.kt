package org.gabrielsantana.tasks.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.materialkolor.rememberDynamicColorScheme
import org.gabrielsantana.tasks.features.settings.ColorType
import org.gabrielsantana.tasks.features.settings.SystemColor

@Composable
actual fun TasksTheme(
    darkTheme: Boolean,
    // Dynamic color is available on Android 12+
    isDynamicColorEnabled: Boolean,
    dynamicColorType: ColorType,
    content: @Composable () -> Unit
) {

    val colorScheme = when {
        isDynamicColorEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && dynamicColorType is SystemColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDynamicColorEnabled && dynamicColorType is ColorType.CustomColor -> {
            rememberDynamicColorScheme(seedColor = dynamicColorType.color, isDark = darkTheme, isAmoled = false)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}