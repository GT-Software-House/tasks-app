package org.gabrielsantana.tasks.features.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.materialkolor.dynamicColorScheme
import org.gabrielsantana.tasks.ui.ColorSchemeProvider

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
        colors.forEachIndexed { index, color ->
            ColorItem(
                color = color,
                isSelected = index == selectedIndex,
                onClick = {
                    onSelect(
                        index,
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