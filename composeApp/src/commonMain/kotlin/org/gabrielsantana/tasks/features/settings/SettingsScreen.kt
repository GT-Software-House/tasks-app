package org.gabrielsantana.tasks.features.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.gabrielsantana.tasks.ui.ColorSchemeProvider
import org.gabrielsantana.tasks.ui.ThemeMode
import org.jetbrains.compose.ui.tooling.preview.Preview

interface ColorType {
    data class CustomColor(val color: Color) : ColorType
}

data class SettingItem(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit
)

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAppearance: () -> Unit,
) {
    val items = listOf(
        SettingItem(
            title = "Appearance",
            icon = Icons.Default.Draw,
            onClick = onNavigateToAppearance
        ),
        SettingItem(
            title = "Logout",
            icon = Icons.AutoMirrored.Default.Logout,
            onClick = { /* TODO add firebase logout here */ }
        )
    )
    SettingsContent(settingItems = items, onNavigateBack = onNavigateBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    settingItems: List<SettingItem>,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues).verticalScroll(rememberScrollState())) {
            settingItems.forEach { item ->
                SettingItemList(settingItem = item)
                if (settingItems.last() != item) HorizontalDivider()
            }
        }
    }
}


@Composable
fun SettingItemList(settingItem: SettingItem, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { settingItem.onClick() }
            .padding(vertical = 16.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = settingItem.icon, contentDescription = null)
        Spacer(Modifier.width(12.dp))
        Text(
            text = settingItem.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
        )
    }
}

@Preview
@Composable
fun DefaultSettingItemListPreview() {
    MaterialTheme {
        SettingItemList(
            settingItem = SettingItem(
                title = "Appearance",
                icon = Icons.Default.Draw,
                onClick = {}
            )
        )
    }
}