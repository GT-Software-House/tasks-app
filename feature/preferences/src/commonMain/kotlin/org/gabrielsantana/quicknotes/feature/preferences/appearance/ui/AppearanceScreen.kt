package org.gabrielsantana.quicknotes.feature.preferences.appearance.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.gabrielsantana.quicknotes.core.components.RadioItem
import org.gabrielsantana.quicknotes.core.components.SelectableItem
import org.gabrielsantana.quicknotes.feature.preferences.appearance.data.model.ThemeMode
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

val colors = setOf(
    Color.Blue,
    Color.Red,
    Color.Green,
    Color.Yellow,
    Color.Cyan,
    Color.Magenta,
    Color.DarkGray
)

@Composable
fun AppearanceScreen(
    onNavigateBack: () -> Unit,
    viewModel: AppearanceViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AppearanceContent(
        uiState = uiState,
        onChangeThemeMode = viewModel::updateThemeModePreference,
        onToggleDynamicColors = viewModel::updateDynamicColorPreference,
        onBackClick = onNavigateBack,
        onSelectColor = {
            viewModel.updateSelectedColorPreference(it.toArgb())
        },
        onToggleAmoled = viewModel::updateAmoledPreference
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppearanceContent(
    uiState: AppearanceUiState,
    onBackClick: () -> Unit,
    onChangeThemeMode: (ThemeMode) -> Unit,
    onToggleDynamicColors: (Boolean) -> Unit,
    onSelectColor: (Color) -> Unit,
    onToggleAmoled: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Appearance") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)

        ) {
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
                        isChecked = option.second == uiState.themeMode,
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
                isChecked = uiState.isDynamicColorsEnabled,
                onToggle = onToggleDynamicColors
            )
            AnimatedVisibility(uiState.isDynamicColorsEnabled) {
                Column {
                    ColorSelector(
                        selectedColor = uiState.selectedSeedColor?.let { Color(it) },
                        onSelectColor = onSelectColor,
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                            .padding(start = 8.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    SelectableItem(
                        label = "Use amoled colors(dark mode only)",
                        isChecked = uiState.isAmoled,
                        onToggle = onToggleAmoled
                    )
                }
            }
        }
    }
}

@Composable
internal fun ColorSelector(
    selectedColor: Color? = null,
    onSelectColor: (Color) -> Unit,
    modifier: Modifier
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        colors.forEach { color ->
            ColorItem(
                color = color,
                isSelected = color == selectedColor,
                onClick = {
                    onSelectColor(color)
                }
            )
        }
    }
}

@Composable
internal fun ColorItem(
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


@Preview
@Composable
private fun DefaultPreviewAppearanceScreen() {
    MaterialTheme {
        AppearanceContent(
            uiState = AppearanceUiState(
                selectedSeedColor = Color.Blue.toArgb(),
                isDynamicColorsEnabled = true
            ),
            onBackClick = {},
            onChangeThemeMode = {},
            onToggleDynamicColors = {},
            onSelectColor = {},
            onToggleAmoled = {}
        )
    }
}