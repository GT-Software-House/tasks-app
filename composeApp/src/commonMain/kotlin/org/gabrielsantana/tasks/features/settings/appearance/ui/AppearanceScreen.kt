package org.gabrielsantana.tasks.features.settings.appearance.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.gabrielsantana.tasks.ui.ThemeMode
import org.koin.compose.viewmodel.koinViewModel

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
        onBackClick = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceContent(
    uiState: AppearanceUiState,
    onBackClick: () -> Unit,
    onChangeThemeMode: (ThemeMode) -> Unit,
    onToggleDynamicColors: (Boolean) -> Unit,
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


        }

    }
}

//@Composable
//expect fun ColorSelector(
//    selectedIndex: Int,
//    onSelect: (index: Int, colorSchemeProvider: ColorSchemeProvider) -> Unit,
//    modifier: Modifier
//)
//
//internal val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta, Color.Cyan)
//
//@Composable
//fun CustomItem(
//    isSelected: Boolean,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier,
//    content: @Composable BoxScope.() -> Unit,
//) {
//    val borderColor by animateColorAsState(
//        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
//    )
//    val borderWidthAnimated by animateDpAsState(if (isSelected) 2.dp else 1.dp)
//    val shape = RoundedCornerShape(16.dp)
//    Box(
//        modifier = modifier
//            .size(40.dp)
//            .border(
//                width = borderWidthAnimated,
//                color = borderColor,
//                shape = shape
//            )
//            .clip(shape)
//            .selectable(
//                selected = isSelected,
//                onClick = onClick
//            )
//            .padding(8.dp),
//        content = content
//    )
//}
//
//@Composable
//fun ColorItem(
//    color: Color,
//    isSelected: Boolean,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val borderColor by animateColorAsState(
//        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
//    )
//    val borderWidthAnimated by animateDpAsState(if (isSelected) 2.dp else 1.dp)
//    val shape = RoundedCornerShape(16.dp)
//    Box(
//        modifier
//            .size(40.dp)
//            .border(
//                width = borderWidthAnimated,
//                color = borderColor,
//                shape = shape
//            )
//            .clip(shape)
//            .selectable(
//                selected = isSelected,
//                onClick = onClick
//            )
//            .padding(8.dp)
//            .background(color = color, shape = CircleShape)
//
//    )
//}
//
//
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
//
//@Preview
//@Composable
//private fun SettingsItemPreview(modifier: Modifier = Modifier) {
//    var isChecked by remember { mutableStateOf(false) }
//    MaterialTheme {
//        SelectableItem(
//            label = "Dark Theme",
//            isChecked = isChecked,
//            onToggle = { isChecked = it }
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun SettingsScreenPreview() {
//    val (themeMode, setThemeMode) = remember { mutableStateOf<ThemeMode>(ThemeMode.System) }
//    val (isDynamicColorsEnabled, setDynamicColors) = remember { mutableStateOf(true) }
//
////    MaterialTheme {
////        Surface(Modifier.fillMaxSize()) {
////            SettingsScreen(
////                themeMode = themeMode,
////                isDynamicColorsEnabled = isDynamicColorsEnabled,
////                onChangeThemeMode = setThemeMode,
////                onToggleDynamicColors = setDynamicColors,
////                onDismissRequest = {  }
////            )
////        }
////    }
//}