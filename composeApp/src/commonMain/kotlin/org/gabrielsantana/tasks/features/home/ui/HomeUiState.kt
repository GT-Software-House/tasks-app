package org.gabrielsantana.tasks.features.home.ui

import org.gabrielsantana.tasks.data.scheduler.QueueSyncStatus
import org.gabrielsantana.tasks.features.settings.TaskFilter

data class HomeUiState(
    val tasks: List<TaskUiModel> = emptyList(),
    val selectedTasksIndex: Set<Int> = emptySet(),
    val selectedTaskFilter: TaskFilter = TaskFilter.ALL,
    val syncStatus: QueueSyncStatus = QueueSyncStatus.Empty
) {
    val isSelectionMode: Boolean
        get() = selectedTasksIndex.isNotEmpty()
}

data class TaskUiModel(
    val id: Int,
    val title: String,
    val description: String,
    val isChecked: Boolean
)
