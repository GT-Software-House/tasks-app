package org.gabrielsantana.tasks.features.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.gabrielsantana.tasks.ui.AppState
import org.gabrielsantana.tasks.ui.ColorSchemeProvider
import org.gabrielsantana.tasks.ui.ThemeMode
import org.jetbrains.compose.ui.tooling.preview.Preview

interface ColorType {
    data class CustomColor(val color: Color) : ColorType
}

@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    //TODO remove it
    isDynamicColorsEnabled: Boolean,
    onChangeThemeMode: (ThemeMode) -> Unit,
    onToggleDynamicColors: (Boolean) -> Unit,
    onColorSchemeProvider: (ColorSchemeProvider) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {
                Text("Settings", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))

                Text("Theme", style = MaterialTheme.typography.titleMedium)

                //TODO probably there is a better way to do this
                val options = listOf(
                    "Dark" to ThemeMode.Dark,
                    "Light" to ThemeMode.Light,
                    "System" to ThemeMode.System
                )

                Column(Modifier.selectableGroup()) {
                    options.forEach { option ->
                        RadioItem(
                            label = option.first,
                            isChecked = option.second == themeMode,
                            onClick = { onChangeThemeMode(option.second) },
                            modifier = Modifier.semantics {
                                contentDescription = option.first
                            }
                        )
                    }
                }

                Text("Colors", style = MaterialTheme.typography.titleMedium)

                SelectableItem(
                    label = "Use dynamic colors",
                    isChecked = isDynamicColorsEnabled,
                    onToggle = onToggleDynamicColors
                )
                var selectedIndex by remember { mutableIntStateOf(0) }
                AnimatedVisibility(isDynamicColorsEnabled) {
                    ColorSelector(
                        selectedIndex = selectedIndex,
                        onSelect = { index, colorSchemeProvider ->
                            selectedIndex = index
                            onColorSchemeProvider(colorSchemeProvider)
                        },
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    )
                }

                Button(onClick = onDismissRequest, Modifier.align(Alignment.End)) {
                    Text("Done")
                }
            }
        }
    }
}





@Composable
expect fun ColorSelector(
    selectedIndex: Int,
    onSelect: (index: Int, colorSchemeProvider: ColorSchemeProvider) -> Unit,
    modifier: Modifier
)

internal val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta, Color.Cyan)

@Composable
fun CustomItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val borderColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    )
    val borderWidthAnimated by animateDpAsState(if (isSelected) 2.dp else 1.dp)
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .size(40.dp)
            .border(
                width = borderWidthAnimated,
                color = borderColor,
                shape = shape
            )
            .clip(shape)
            .selectable(
                selected = isSelected,
                onClick = onClick
            )
            .padding(8.dp),
        content = content
    )
}

@Composable
fun ColorItem(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    )
    val borderWidthAnimated by animateDpAsState(if (isSelected) 2.dp else 1.dp)
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier
            .size(40.dp)
            .border(
                width = borderWidthAnimated,
                color = borderColor,
                shape = shape
            )
            .clip(shape)
            .selectable(
                selected = isSelected,
                onClick = onClick
            )
            .padding(8.dp)
            .background(color = color, shape = CircleShape)

    )
}


@Composable
fun RadioItem(
    label: String,
    isChecked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = isChecked,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isChecked,
            onClick = null // null recommended for accessibility with screenreaders
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun SelectableItem(
    label: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .toggleable(
                value = isChecked,
                onValueChange = { onToggle(!isChecked) },
                role = Role.Checkbox
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = null // null recommended for accessibility with screenreaders
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp, top = 18.dp, bottom = 18.dp)
        )
    }
}

@Preview
@Composable
private fun SettingsItemPreview(modifier: Modifier = Modifier) {
    var isChecked by remember { mutableStateOf(false) }
    MaterialTheme {
        SelectableItem(
            label = "Dark Theme",
            isChecked = isChecked,
            onToggle = { isChecked = it }
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    val (themeMode, setThemeMode) = remember { mutableStateOf<ThemeMode>(ThemeMode.System) }
    val (isDynamicColorsEnabled, setDynamicColors) = remember { mutableStateOf(true) }

//    MaterialTheme {
//        Surface(Modifier.fillMaxSize()) {
//            SettingsScreen(
//                themeMode = themeMode,
//                isDynamicColorsEnabled = isDynamicColorsEnabled,
//                onChangeThemeMode = setThemeMode,
//                onToggleDynamicColors = setDynamicColors,
//                onDismissRequest = {  }
//            )
//        }
//    }
}