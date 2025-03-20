package org.gabrielsantana.quicknotes.feature.home.ui

import org.gabrielsantana.quicknotes.data.task.scheduler.QueueSyncStatus
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


data class HomeUiState(
    val tasks: List<TaskUiModel> = emptyList(),
    val selectedTasksUuid: Set<Uuid> = emptySet(),
    val selectedTaskFilter: TaskFilter = TaskFilter.ALL,
    val syncStatus: QueueSyncStatus = QueueSyncStatus.Empty,
    val isRefreshing: Boolean = false,
    val retryAction: (() -> Unit)? = null,
) {
    val isSelectionMode: Boolean
        get() = selectedTasksUuid.isNotEmpty()
}
