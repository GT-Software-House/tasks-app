package org.gabrielsantana.tasks.features.settings

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data object SystemColor : ColorType

@Composable
actual fun ColorSelector(
    colors: List<Color>,
    selectedColor: ColorType,
    onSelectColor: (ColorType) -> Unit,
    modifier: Modifier
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            CustomItem(
                isSelected = selectedColor is SystemColor,
                onClick = { onSelectColor(SystemColor) }
            ) { Icon(Icons.Default.PhoneAndroid, null) }
        }
        colors.forEach { color ->
            ColorItem(
                color = color,
                isSelected = selectedColor is ColorType.CustomColor && selectedColor.color == color,
                onClick = { onSelectColor(ColorType.CustomColor(color)) }
            )
        }
    }
}