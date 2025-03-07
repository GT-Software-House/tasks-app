package org.gabrielsantana.tasks.features.create.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateTaskScreen(
    onNavigateBack: (taskAction: TaskAction?) -> Unit,
    viewModel: CreateTaskViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.taskAction) {
        if (uiState.taskAction != null) {
            onNavigateBack(uiState.taskAction)
        }
    }

    CreateTaskContent(
        uiState = uiState,
        onNavigationBackClick = { onNavigateBack(null) },
        onCreateClick = viewModel::createTask,
        onDescriptionChange = viewModel::updateDescription,
        onTitleChange = viewModel::updateTitle,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskContent(
    uiState: CreateTaskUiState,
    onNavigationBackClick: () -> Unit,
    onCreateClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    val title = if (uiState.isEditMode) "Edit task" else "Create task"
                    Text(title)
                },
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
                        val label = if (uiState.isEditMode) "Update" else "Create"
                        Text(label)
                    }
                    Spacer(Modifier.width(4.dp))
                }
            )
        },
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(it).padding(24.dp)) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = onTitleChange,
                label = { Text("Title") },
                supportingText = {
                    if (uiState.isTitleInvalid == true)
                        Text("Não pode ser vazio")
                },
                isError = uiState.isTitleInvalid == true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                isError = uiState.isDescriptionInvalid == true,
                label = { Text("Description") },
                supportingText = {
                    if (uiState.isDescriptionInvalid == true)
                        Text("Não pode ser vazio")
                },
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
            onTitleChange = {},
            onDescriptionChange = {},
            uiState = CreateTaskUiState()
        )
    }
}
