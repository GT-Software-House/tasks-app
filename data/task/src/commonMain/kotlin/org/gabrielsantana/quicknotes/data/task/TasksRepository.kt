package org.gabrielsantana.quicknotes.data.task

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.gabrielsantana.quicknotes.data.task.model.Task
import org.gabrielsantana.quicknotes.data.task.scheduler.TaskSyncScheduler
import org.gabrielsantana.quicknotes.data.task.source.local.TasksLocalDataSource
import org.gabrielsantana.quicknotes.data.task.source.local.model.asTask
import kotlin.uuid.Uuid

interface TasksRepository : Syncer {
    fun getTasks(): Flow<List<Task>>
    fun deleteTask(uuid: Uuid)
    fun getTaskById(uuid: Uuid): Task?
    fun updateTask(uuid: Uuid, isChecked: Boolean)
    fun updateTaskTitleAndDescription(uuid: Uuid, title: String, description: String)
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

    override fun deleteTask(uuid: Uuid) {
        localDataSource.delete(uuid)
        taskSyncScheduler.scheduleDelete(uuid)
    }

    override fun getTaskById(uuid: Uuid): Task? = localDataSource.getById(uuid)?.asTask()

    override fun updateTask(uuid: Uuid, isChecked: Boolean) {
        localDataSource.updateIsChecked(uuid, isChecked)
        taskSyncScheduler.scheduleTaskUpdate(uuid)
    }

    override fun updateTaskTitleAndDescription(uuid: Uuid, title: String, description: String) {
        localDataSource.updateTitleAndDescription(uuid, title, description)
        taskSyncScheduler.scheduleTaskUpdate(uuid)
    }

    override fun createTask(title: String, description: String, isCompleted: Boolean) {
        val newTask = localDataSource.insert(title, description, isCompleted)
        taskSyncScheduler.scheduleTask(taskUuid = newTask.uuid)
    }
}