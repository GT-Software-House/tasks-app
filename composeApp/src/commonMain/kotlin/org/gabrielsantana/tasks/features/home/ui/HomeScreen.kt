@file:OptIn(ExperimentalMaterial3Api::class)

package org.gabrielsantana.tasks.features.home.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.gabrielsantana.tasks.features.settings.TaskFilter
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
    onSelectTaskIndex: (Int) -> Unit,
    onClearSelection: () -> Unit,
    onTaskCheckedChange: (newValue: Boolean, model: TaskUiModel) -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()

    if (showDeleteDialog) {
        DeleteWarningDialog(
            onDismissRequest = { showDeleteDialog = !showDeleteDialog },
            onDeleteClick = onDeleteClick
        )
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            AnimatedContent(
                uiState.isSelectionMode
            ) {
                if (it) {
                    SelectionTopAppBar(
                        uiState,
                        scrollBehavior,
                        onClearSelection,
                        onDeleteClick = { showDeleteDialog = !showDeleteDialog }
                    )
                } else {
                    NormalTopAppBar(scrollBehavior, uiState, scope, tooltipState, onSettingsClick)
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
                            shape = SegmentedButtonDefaults.itemShape(index, count = options.size),
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

@Composable
fun DeleteWarningDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onDeleteClick()
                onDismissRequest()
            }) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) { Text("Cancel") }
        },
        title = { Text("Delete selected tasks?") },
        text = { Text("This action can't be undone") },
        modifier = modifier
    )

}

@Composable
private fun SelectionTopAppBar(
    uiState: HomeUiState,
    scrollBehavior: TopAppBarScrollBehavior,
    onClearSelection: () -> Unit,
    onDeleteClick: () -> Unit
) {
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
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, "Clear Tasks")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NormalTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    uiState: HomeUiState,
    scope: CoroutineScope,
    tooltipState: TooltipState,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = { Text("Your tasks") },
        scrollBehavior = scrollBehavior,
        actions = {
            AnimatedVisibility(uiState.haveTasksWaitingSync) {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                    tooltip = {
                        RichTooltip(
                            title = { Text("Sync") },
                        ) {
                            Text("There's no synced data.\nConnect to a network.")
                        }
                    },
                    state = tooltipState
                ) {
                    IconButton(onClick = {
                        scope.launch { tooltipState.show() }
                    }) {
                        Icon(Icons.Default.CloudOff, null)
                    }
                }
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, null)
            }
        }
    )
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
        Row(
            Modifier.padding(top = 12.dp, bottom = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
private fun TaskItemPreview() {
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