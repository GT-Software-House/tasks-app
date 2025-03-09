package org.gabrielsantana.quicknotes.features.settings

import androidx.compose.ui.graphics.vector.ImageVector

data class SettingItem(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit
)
