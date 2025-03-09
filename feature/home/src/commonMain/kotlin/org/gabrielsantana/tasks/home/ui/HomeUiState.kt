package org.gabrielsantana.tasks.home.ui

import org.gabrielsantana.tasks.data.scheduler.QueueSyncStatus

data class HomeUiState(
    val tasks: List<TaskUiModel> = emptyList(),
    val selectedTasksIndex: Set<Int> = emptySet(),
    val selectedTaskFilter: TaskFilter = TaskFilter.ALL,
    val syncStatus: QueueSyncStatus = QueueSyncStatus.Empty,
    val isRefreshing: Boolean = false,
    val retryAction: (() -> Unit)? = null,
) {
    val isSelectionMode: Boolean
        get() = selectedTasksIndex.isNotEmpty()
}
