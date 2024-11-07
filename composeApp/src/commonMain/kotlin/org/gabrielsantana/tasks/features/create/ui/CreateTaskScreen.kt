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
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateTaskViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateTaskContent(
        uiState = uiState,
        onNavigationBackClick = onNavigateBack,
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
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.taskCreatedSuccessfully) {
        if (uiState.taskCreatedSuccessfully) {
            scope.launch {
                snackbarHostState.showSnackbar("Task created", "Go back tasks")
                onNavigationBackClick()
            }
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
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
                    if (uiState.isDescriptionInvalid == true)
                        Text("A descrição não pode ser vazia")
                },
                isError = uiState.isDescriptionInvalid == true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                isError = uiState.isDescriptionInvalid == true,
                label = { Text("Description") },
                supportingText = {
                    if (uiState.isDescriptionInvalid == true)
                        Text("A descrição não pode ser vazia")
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
