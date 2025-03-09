package org.gabrielsantana.quicknotes.data.task.scheduler

internal interface TaskSyncScheduler {
    fun scheduleTask(taskUuid: String)
    fun scheduleDelete(taskUuid: String)
    fun scheduleTaskUpdate(taskUuid: String)
}

sealed interface QueueSyncStatus {
    data object Empty : QueueSyncStatus
    data object Waiting : QueueSyncStatus
    data object Syncing : QueueSyncStatus
}
