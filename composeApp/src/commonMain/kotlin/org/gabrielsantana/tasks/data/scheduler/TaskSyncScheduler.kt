package org.gabrielsantana.tasks.data.scheduler

import kotlinx.coroutines.flow.Flow

interface TaskSyncScheduler {
    suspend fun scheduleTask(taskId: Long)
    //TODO: improve name
    fun tasksWaitingSync(): Flow<QueueSyncStatus>
}

sealed interface QueueSyncStatus {
    data object Empty : QueueSyncStatus
    data object Waiting : QueueSyncStatus
    data object Syncing : QueueSyncStatus
}
