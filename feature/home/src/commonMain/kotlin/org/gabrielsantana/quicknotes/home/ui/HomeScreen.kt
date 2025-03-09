@file:OptIn(ExperimentalMaterial3Api::class)

package org.gabrielsantana.quicknotes.home.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.gabrielsantana.quicknotes.data.task.TasksRepository
import org.gabrielsantana.quicknotes.data.task.scheduler.QueueSyncStatus
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.Random

@Composable
fun HomeScreen(
    onNavigateToCreateTask: () -> Unit,
    onNavigateToEditTask: (taskUuid: String) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    tasksRepository: TasksRepository = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            tasksRepository.reactiveSync()
        }
    }

    LaunchedEffect(uiState.retryAction) {
        if (uiState.retryAction != null) {
            val result = snackbarHostState.showSnackbar("An error occurred", "Retry",)
            if (result == SnackbarResult.ActionPerformed) viewModel.doRetryAction()
        }
    }

    HomeContent(
        uiState = uiState,
        hostState = snackbarHostState,
        onCreateTaskClick = onNavigateToCreateTask,
        onTaskCheckedChange = viewModel::updateTask,
        onSelectTaskFilter = viewModel::selectTaskFilter,
        onSelectTaskIndex = viewModel::selectTask,
        onClearSelection = viewModel::clearSelectedTasks,
        onDeleteClick = viewModel::deleteSelectedTasks,
        onSettingsClick = onNavigateToSettings,
        onTaskClick = onNavigateToEditTask,
        onRefresh = viewModel::refresh
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
    onTaskClick: (String) -> Unit,
    onTaskCheckedChange: (newValue: Boolean, model: TaskUiModel) -> Unit,
    onDeleteClick: () -> Unit,
    onRefresh: () -> Unit,
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
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.padding(paddingValues)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    val options = TaskFilter.entries
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        options.forEachIndexed { index, filter ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index,
                                    count = options.size
                                ),
                                onClick = { onSelectTaskFilter(filter) },
                                selected = filter == uiState.selectedTaskFilter
                            ) {
                                Text(filter.label)
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
                items(uiState.tasks, key = { task -> task.uuid }) { task ->
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
                                        onTaskClick(task.uuid)
                                    }
                                }
                            ).fillMaxWidth(),
                    )
                }
            }
            if (uiState.tasks.isEmpty()) {
                EmptyTaskListWarning(Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun EmptyTaskListWarning(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            Icons.Default.SentimentDissatisfied,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Looks like there's no tasks yet.",
            style = MaterialTheme.typography.titleMedium,
        )
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
    val syncingIcon = @Composable { CloudSyncingProgress() }
    val waitingIcon = @Composable { Icon(Icons.Default.CloudOff, contentDescription = "No Sync") }
    val pair = when (uiState.syncStatus) {
        QueueSyncStatus.Empty -> null
        QueueSyncStatus.Syncing -> syncingIcon to "Data syncing in progress."
        QueueSyncStatus.Waiting -> waitingIcon to "There's no synced data.\nConnect to a network."
    }

    TopAppBar(
        title = { Text("Your tasks") },
        scrollBehavior = scrollBehavior,
        actions = {
            if (pair != null) {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                    tooltip = {
                        RichTooltip(
                            title = { Text("Sync") }
                        ) {
                            Text(pair.second)
                        }
                    },
                    state = tooltipState
                ) {
                    IconButton(
                        onClick = { scope.launch { tooltipState.show() } },
                        content = pair.first
                    )
                }
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    )
}

@Composable
private fun CloudSyncingProgress() {
    val rotation by rememberInfiniteTransition().animateFloat(
        initialValue = 0F,
        targetValue = 360F,
        animationSpec =
            infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            )
    )
    Icon(Icons.Default.CloudSync, null, Modifier.rotate(rotation))
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
        TaskUiModel(Random.nextInt().toString(), "Task 1", "Description 1", false),
        TaskUiModel(Random.nextInt().toString(), "Task 2", "Description 2", true),
        TaskUiModel(Random.nextInt().toString(), "Task 3", "Description 3", false),
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
        onSettingsClick = {},
        onTaskClick = {},
        onRefresh = {}
    )
}

@Preview
@Composable
private fun EmptyTasksPreview() {
    HomeContent(
        uiState = HomeUiState(
            tasks = listOf(),
            selectedTasksIndex = setOf(1, 2)
        ),
        onCreateTaskClick = {},
        onTaskCheckedChange = { _, _ -> },
        onSelectTaskFilter = {},
        onSelectTaskIndex = {},
        onClearSelection = {},
        onDeleteClick = {},
        onSettingsClick = {},
        onRefresh = {},
        onTaskClick = {}
    )
}


@Composable
@Preview
private fun NormalTopAppBarOfflinePreview() {
    MaterialTheme {
        Scaffold {
            NormalTopAppBar(
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                uiState = HomeUiState(syncStatus = QueueSyncStatus.Waiting),
                scope = rememberCoroutineScope(),
                tooltipState = rememberTooltipState(true),
            ) {}
        }
    }
}

@Composable
@Preview
private fun NormalTopAppBarTipOpenedPreview() {
    MaterialTheme {
        Scaffold {
            NormalTopAppBar(
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                uiState = HomeUiState(syncStatus = QueueSyncStatus.Syncing),
                scope = rememberCoroutineScope(),
                tooltipState = rememberTooltipState(true),
            ) {}
        }
    }
}