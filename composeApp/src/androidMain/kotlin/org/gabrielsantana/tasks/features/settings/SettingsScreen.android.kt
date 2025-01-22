package org.gabrielsantana.tasks.features.settings

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.materialkolor.dynamicColorScheme
import org.gabrielsantana.tasks.ui.ColorSchemeProvider

data object SystemColor : ColorType

@Composable
actual fun ColorSelector(
    selectedIndex: Int,
    onSelect: (index: Int, colorSchemeProvider: ColorSchemeProvider) -> Unit,
    modifier: Modifier
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            CustomItem(
                isSelected = selectedIndex == 0,
                onClick = {
                    onSelect(0, ColorSchemeProvider { darkTheme ->
                        val context = LocalContext.current
                        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
                            context
                        )
                    })
                }
            ) { Icon(Icons.Default.PhoneAndroid, null) }
        }
        colors.forEachIndexed { index, color ->
            ColorItem(
                color = color,
                isSelected = (index + 1) == selectedIndex,
                onClick = {
                    onSelect(
                        index + 1,
                        ColorSchemeProvider { darkTheme ->
                            dynamicColorScheme(
                                seedColor = color,
                                isDark = darkTheme,
                                isAmoled = false
                            )
                        }
                    )
                }
            )
        }
    }
}