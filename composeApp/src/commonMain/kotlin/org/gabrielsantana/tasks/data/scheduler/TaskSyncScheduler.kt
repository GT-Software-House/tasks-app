package org.gabrielsantana.tasks.data.scheduler

import kotlinx.coroutines.flow.Flow

interface TaskSyncScheduler {
    fun scheduleTask(taskUuid: String)
    fun scheduleTaskUpdate(taskUuid: String)

    //TODO: improve name
    fun tasksWaitingSync(): Flow<QueueSyncStatus>
}

sealed interface QueueSyncStatus {
    data object Empty : QueueSyncStatus
    data object Waiting : QueueSyncStatus
    data object Syncing : QueueSyncStatus
}
