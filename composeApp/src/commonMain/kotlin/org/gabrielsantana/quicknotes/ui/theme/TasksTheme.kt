package org.gabrielsantana.quicknotes.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.materialkolor.rememberDynamicColorScheme

val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

//@Composable
//expect fun TasksTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    // Dynamic color is available on Android 12+
//    isDynamicColorEnabled: Boolean,
//    dynamicColorType: ColorType,
//    content: @Composable () -> Unit
//)

@Composable
fun TasksTheme(
    darkTheme: Boolean,
    isDynamicColorEnabled: Boolean,
    dynamicColorSeed: Color? = null,
    isAmoled: Boolean = false,
    content: @Composable () -> Unit
) {

    val colorScheme = when {
        isDynamicColorEnabled && dynamicColorSeed != null -> {
            rememberDynamicColorScheme(seedColor = dynamicColorSeed, isDark = darkTheme, isAmoled = isAmoled)
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