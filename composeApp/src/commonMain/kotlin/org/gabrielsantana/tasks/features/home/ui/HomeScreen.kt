package org.gabrielsantana.tasks.features.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun HomeScreen(
    onNavigateToCreateTask: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    HomeContent(onCreateTaskClick = onNavigateToCreateTask)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    onCreateTaskClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateTaskClick) {
                Icon(Icons.Default.Add, contentDescription = "Create task")
            }
        }
    ) {

    }
}

@Preview
@Composable
private fun DefaultPreview() {
    HomeContent(
        onCreateTaskClick = {}
    )
}