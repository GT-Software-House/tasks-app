package org.gabrielsantana.tasks.features.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
        colors.forEach { color ->
            ColorItem(
                color = color,
                isSelected = selectedColor is ColorType.CustomColor && selectedColor.color == color,
                onClick = { onSelectColor(ColorType.CustomColor(color)) }
            )
        }
    }
}