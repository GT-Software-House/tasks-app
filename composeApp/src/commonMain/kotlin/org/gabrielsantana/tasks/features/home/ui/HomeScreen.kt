package org.gabrielsantana.tasks.features.home.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun HomeScreen(
    //TODO improve task create
    taskCreated: Boolean,
    onTaskCreated: () -> Unit,
    onNavigateToCreateTask: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val hostState = remember { SnackbarHostState() }

    LaunchedEffect(taskCreated) {
        if (taskCreated) {
            hostState.showSnackbar("Task created")
            onTaskCreated()
        }
    }

    HomeContent(
        uiState = uiState,
        hostState = hostState,
        onCreateTaskClick = onNavigateToCreateTask,
        onTaskCheckedChange = viewModel::updateTask,
        onSelectTaskFilter = viewModel::selectTaskFilter,
        onSelectTaskIndex = viewModel::selectTask,
        onClearSelection = viewModel::clearSelectedTasks,
        onDeleteClick = viewModel::deleteSelectedTasks,
        onSettingsClick = onNavigateToSettings
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    uiState: HomeUiState,
    hostState: SnackbarHostState = remember { SnackbarHostState() },
    onCreateTaskClick: () -> Unit,
    onSelectTaskFilter: (TaskFilter) -> Unit,
    onSettingsClick: () -> Unit,
    onSelectTaskIndex: (Int ) -> Unit,
    onClearSelection: () -> Unit,
    onTaskCheckedChange: (newValue: Boolean, model: TaskUiModel) -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = !showDeleteDialog },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteClick()
                    showDeleteDialog = !showDeleteDialog
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = !showDeleteDialog }
                ) { Text("Cancel") }
            },
            title = { Text("Delete selected tasks?") },
            text = { Text("This action can't be undone") }
        )
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            AnimatedContent(
                uiState.isSelectionMode
            ) {
                if (it) {
                    TopAppBar(
                        title = { Text(uiState.selectedTasksIndex.size.toString()) },
                        scrollBehavior = scrollBehavior,
                        navigationIcon = {
                            IconButton(onClick = onClearSelection) {
                                Icon(
                                    Icons.AutoMirrored.Default.ArrowBack,
                                    "Close selection"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { showDeleteDialog = !showDeleteDialog }) {
                                Icon(Icons.Default.Delete, "Clear Tasks")
                            }
                        }
                    )
                } else {
                    TopAppBar(
                        title = { Text("Your tasks") },
                        scrollBehavior = scrollBehavior,
                        actions = {
                            IconButton(onClick = onSettingsClick) {
                                Icon(Icons.Default.Settings, null)
                            }
                        }
                    )
                }
            }

        },
        snackbarHost = { SnackbarHost(hostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateTaskClick) {
                Icon(Icons.Default.Add, contentDescription = "Create task")
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(paddingValues)
        ) {
            item {
                val options = TaskFilter.entries
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.forEachIndexed { index, filter ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                            onClick = { onSelectTaskFilter(filter) },
                            selected = filter == uiState.selectedTaskFilter
                        ) {
                            Text(filter.label)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
            items(uiState.tasks, key = { task -> task.id }) { task ->
                val taskIndex = uiState.tasks.indexOf(task)
                TaskItem(
                    title = task.title,
                    description = task.description,
                    isChecked = task.isChecked,
                    isCheckable = !uiState.isSelectionMode,
                    onCheckedChange = { onTaskCheckedChange(it, task) },
                    isSelected = uiState.selectedTasksIndex.contains(uiState.tasks.indexOf(task)),
                    modifier = Modifier
                        .animateItem()
                        .clip(CardDefaults.shape)
                        .combinedClickable(
                            onLongClick = {
                                if (!uiState.isSelectionMode) {
                                    onSelectTaskIndex(taskIndex)
                                }
                            },
                            onClick = {
                                if (uiState.isSelectionMode) {
                                    onSelectTaskIndex(taskIndex)
                                } else {
                                    //some event for click, like expand the cards
                                }
                            }
                        ).fillMaxWidth(),
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskItem(
    title: String,
    description: String,
    isChecked: Boolean,
    isCheckable: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = if (isSelected) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else CardDefaults.cardColors()
    ) {
        Row(Modifier.padding(top = 12.dp, bottom = 12.dp, end = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            AnimatedVisibility(isSelected) {
                Row(modifier = Modifier.padding(start = 12.dp)) {
                    Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
            }
            AnimatedVisibility(isCheckable) {
                Checkbox(checked = isChecked, onCheckedChange = onCheckedChange)
            }
        }
    }
}

@Preview
@Composable
private fun TaskItemPreview(modifier: Modifier = Modifier) {
    MaterialTheme {
        TaskItem(
            "teste",
            "descrioptipmn",
            true,
            false,
            {},
            true,
        )
    }
}


@Preview
@Composable
private fun DefaultPreview() {
    val tasks = listOf(
        TaskUiModel(1, "Task 1", "Description 1", false),
        TaskUiModel(2, "Task 2", "Description 2", true),
        TaskUiModel(3, "Task 3", "Description 3", false),
    )
    HomeContent(
        uiState = HomeUiState(
            tasks = tasks,
            selectedTasksIndex = setOf(1, 2)
        ),
        onCreateTaskClick = {},
        onTaskCheckedChange = { _, _ -> },
        onSelectTaskFilter = {},
        onSelectTaskIndex = {},
        onClearSelection = {},
        onDeleteClick = {},
        onSettingsClick = {}
    )
}