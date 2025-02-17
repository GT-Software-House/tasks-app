package org.gabrielsantana.tasks.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.gabrielsantana.tasks.data.model.Task
import org.gabrielsantana.tasks.data.scheduler.TaskSyncScheduler
import org.gabrielsantana.tasks.data.source.local.TasksLocalDataSource
import org.gabrielsantana.tasks.data.source.local.model.asRemoteTask
import org.gabrielsantana.tasks.data.source.local.model.asTask
import org.gabrielsantana.tasks.data.source.remote.TasksRemoteDataSource

class TasksRepository(
    private val taskSyncScheduler: TaskSyncScheduler,
    private val localDataSource: TasksLocalDataSource,
    private val remoteDataSource: TasksRemoteDataSource,
) {
    fun getTasks(): Flow<List<Task>> = localDataSource.getAll().map { it.map { entity -> entity.asTask() } }

    fun deleteTask(uuid: String) = localDataSource.delete(uuid)

    fun updateTask(uuid: String, isChecked: Boolean) {
        localDataSource.updateIsChecked(uuid, isChecked)
        taskSyncScheduler.scheduleTaskUpdate(uuid)
    }

    fun createTask(title: String, description: String, isCompleted: Boolean = false) {
        val newTask = localDataSource.insert(title, description, isCompleted)
        taskSyncScheduler.scheduleTask(taskUuid = newTask.uuid)
    }

    /**
     * @return if the data was synced successfully
     */
    suspend fun syncRemoteWithLocal(taskUuid: String): Boolean {
        //here we can have a problem on finding the task or the task just has been deleted. How to diff the cases?
        val task = localDataSource.getById(taskUuid) ?: return true
        return remoteDataSource.update(task.asRemoteTask())
    }

}