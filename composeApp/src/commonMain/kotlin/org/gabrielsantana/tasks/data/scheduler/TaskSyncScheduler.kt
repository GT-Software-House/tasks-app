package org.gabrielsantana.tasks.data.scheduler

import kotlinx.coroutines.flow.Flow

interface TaskSyncScheduler {
    suspend fun scheduleTask(taskId: Long)
    //TODO: improve name
    fun tasksWaitingSync(): Flow<Boolean>
}
