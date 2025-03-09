package org.gabrielsantana.quicknotes.feature.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.gabrielsantana.quicknotes.feature.preferences.appearance.ui.AppearanceScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Serializable
internal data object PreferencesMenuRoute

@Serializable
internal data object AppearanceRoute

inline fun <reified T: Any> NavGraphBuilder.preferencesNavigation(navController: NavController) {
    navigation<T>(startDestination = PreferencesMenuRoute) {
        composable<PreferencesMenuRoute> {
            SettingsScreen(
                onNavigateToAppearance = {
                    navController.navigate(AppearanceRoute)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<AppearanceRoute> {
            AppearanceScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }

}

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