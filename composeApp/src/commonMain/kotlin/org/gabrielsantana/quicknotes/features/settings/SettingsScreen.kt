package org.gabrielsantana.quicknotes.features.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    supabaseClient: SupabaseClient = koinInject()
) {
    var isLogoutWarningDialogOpen by remember { mutableStateOf(false) }
    fun toggleLogoutWarningDialog() {
        isLogoutWarningDialogOpen = !isLogoutWarningDialogOpen
    }
    val scope = rememberCoroutineScope()
    val items = remember {
        listOf(
            SettingItem(
                title = "Appearance",
                icon = Icons.Default.Draw,
                onClick = onNavigateToAppearance
            ),
            SettingItem(
                title = "Logout",
                icon = Icons.AutoMirrored.Default.Logout,
                onClick = ::toggleLogoutWarningDialog
            )
        )

    }
    if (isLogoutWarningDialogOpen) LogoutWarningDialog(
        onDismissRequest = ::toggleLogoutWarningDialog,
        onLogoutClick = {
            toggleLogoutWarningDialog()
            scope.launch {
                supabaseClient.auth.signOut()
            }
        }
    )
    SettingsContent(settingItems = items, onNavigateBack = onNavigateBack)
}

@Composable
fun LogoutWarningDialog(
    onLogoutClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = { TextButton(onClick = onLogoutClick) { Text("Sign out") } },
        dismissButton = { TextButton(onClick = onDismissRequest) { Text("Cancel") } },
        title = { Text("Sign out from the app?") },
        text = { Text("You to be logged to use the app.") },
        icon = { Icon(Icons.AutoMirrored.Default.Logout, null) },
        modifier = modifier
    )
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