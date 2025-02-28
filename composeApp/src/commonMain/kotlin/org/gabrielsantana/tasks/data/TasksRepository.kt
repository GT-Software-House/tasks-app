package org.gabrielsantana.tasks.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.gabrielsantana.tasks.data.model.Task
import org.gabrielsantana.tasks.data.scheduler.TaskSyncScheduler
import org.gabrielsantana.tasks.data.source.local.TasksLocalDataSource
import org.gabrielsantana.tasks.data.source.local.model.asTask

class TasksRepository(
    private val taskSyncScheduler: TaskSyncScheduler,
    private val localDataSource: TasksLocalDataSource,
    private val taskSyncer: TaskSyncer
) : Syncer by taskSyncer {
    companion object {
        private const val TAG = "TasksRepository"
    }

    fun getTasks(): Flow<List<Task>> = localDataSource.getAll().map {
        it.map { entity -> entity.asTask() }.sortedBy { it.createdAt.epochSeconds }
    }

    fun deleteTask(uuid: String) {
        localDataSource.delete(uuid)
        taskSyncScheduler.scheduleDelete(uuid)
    }

    fun getTaskById(uuid: String): Task? = localDataSource.getById(uuid)?.asTask()

    fun updateTask(uuid: String, isChecked: Boolean) {
        localDataSource.updateIsChecked(uuid, isChecked)
        taskSyncScheduler.scheduleTaskUpdate(uuid)
    }

    fun updateTaskTitleAndDescription(uuid: String, title: String, description: String) {
        localDataSource.updateTitleAndDescription(uuid, title, description)
        taskSyncScheduler.scheduleTaskUpdate(uuid)
    }

    fun createTask(title: String, description: String, isCompleted: Boolean = false) {
        val newTask = localDataSource.insert(title, description, isCompleted)
        taskSyncScheduler.scheduleTask(taskUuid = newTask.uuid)
    }
}
