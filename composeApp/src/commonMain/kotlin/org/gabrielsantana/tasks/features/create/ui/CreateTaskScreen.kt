package org.gabrielsantana.tasks.features.create.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateTaskViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateTaskContent(
        onNavigationBackClick = onNavigateBack,
        onCreateClick = viewModel::createTask,
        onDescriptionChange = viewModel::updateDescription,
        onTitleChange = viewModel::updateTitle,
        title = uiState.title,
        description = uiState.description,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskContent(
    onNavigationBackClick: () -> Unit,
    onCreateClick: () -> Unit,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create task") },
                navigationIcon = {
                    IconButton(onClick = onNavigationBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                },
                actions = {
                    Button(onClick = onCreateClick) {
                        Text("Create")
                    }
                }
            )
        },
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(it).padding(24.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MaterialTheme {
        CreateTaskContent(
            onNavigationBackClick = {},
            onCreateClick = {},
            title = "",
            onTitleChange = {},
            description = "",
            onDescriptionChange = {}
        )
    }
}
