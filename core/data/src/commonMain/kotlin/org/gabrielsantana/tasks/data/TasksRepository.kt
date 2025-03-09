package org.gabrielsantana.tasks.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.gabrielsantana.tasks.data.model.Task
import org.gabrielsantana.tasks.data.scheduler.TaskSyncScheduler
import org.gabrielsantana.tasks.data.source.local.TasksLocalDataSource
import org.gabrielsantana.tasks.data.source.local.model.asTask

interface TasksRepository : Syncer {
    fun getTasks(): Flow<List<Task>>
    fun deleteTask(uuid: String)
    fun getTaskById(uuid: String): Task?
    fun updateTask(uuid: String, isChecked: Boolean)
    fun updateTaskTitleAndDescription(uuid: String, title: String, description: String)
    fun createTask(title: String, description: String, isCompleted: Boolean = false)
}

internal class TasksRepositoryImpl(
    private val taskSyncScheduler: TaskSyncScheduler,
    private val localDataSource: TasksLocalDataSource,
    private val taskSyncer: TaskSyncer
) : TasksRepository, Syncer by taskSyncer {
    companion object {
        private const val TAG = "TasksRepository"
    }

    override fun getTasks(): Flow<List<Task>> = localDataSource.getAll().map {
        it.map { entity -> entity.asTask() }.sortedBy { it.createdAt.epochSeconds }
    }

    override fun deleteTask(uuid: String) {
        localDataSource.delete(uuid)
        taskSyncScheduler.scheduleDelete(uuid)
    }

    override fun getTaskById(uuid: String): Task? = localDataSource.getById(uuid)?.asTask()

    override fun updateTask(uuid: String, isChecked: Boolean) {
        localDataSource.updateIsChecked(uuid, isChecked)
        taskSyncScheduler.scheduleTaskUpdate(uuid)
    }

    override fun updateTaskTitleAndDescription(uuid: String, title: String, description: String) {
        localDataSource.updateTitleAndDescription(uuid, title, description)
        taskSyncScheduler.scheduleTaskUpdate(uuid)
    }

    override fun createTask(title: String, description: String, isCompleted: Boolean) {
        val newTask = localDataSource.insert(title, description, isCompleted)
        taskSyncScheduler.scheduleTask(taskUuid = newTask.uuid)
    }
}