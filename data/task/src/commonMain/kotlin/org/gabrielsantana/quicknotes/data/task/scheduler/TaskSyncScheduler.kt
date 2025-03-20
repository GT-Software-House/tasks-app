package org.gabrielsantana.quicknotes.data.task.scheduler

import kotlin.uuid.Uuid

internal interface TaskSyncScheduler {
    fun scheduleTask(taskUuid: Uuid)
    fun scheduleDelete(taskUuid: Uuid)
    fun scheduleTaskUpdate(taskUuid: Uuid)
}

sealed interface QueueSyncStatus {
    data object Empty : QueueSyncStatus
    data object Waiting : QueueSyncStatus
    data object Syncing : QueueSyncStatus
}
