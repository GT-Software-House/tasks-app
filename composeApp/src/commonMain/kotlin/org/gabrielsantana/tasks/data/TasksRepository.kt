package org.gabrielsantana.tasks.data

import dev.jordond.connectivity.Connectivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.gabrielsantana.tasks.data.model.Task
import org.gabrielsantana.tasks.data.source.local.TasksLocalDataSource
import org.gabrielsantana.tasks.data.source.local.model.asTask
import org.gabrielsantana.tasks.data.source.remote.TasksRemoteDataSource

class TasksRepository(
    private val connectivity: Connectivity,
    private val remoteDataSource: TasksRemoteDataSource,
    private val localDataSource: TasksLocalDataSource
) {
    fun getTasks(): Flow<List<Task>> = localDataSource.getAll().map { it.map { entity -> entity.asTask() } }

    fun deleteTask(id: Long) = localDataSource.delete(id)

    fun updateTask(id: Long, isChecked: Boolean) = localDataSource.updateIsChecked(id, isChecked)

    suspend fun createTask(title: String, description: String, isCompleted: Boolean = false) {
        val newTask = localDataSource.insert(title, description, isCompleted)
        if (connectivity.status() is Connectivity.Status.Connected) {
            remoteDataSource.insert(
                title = newTask.title,
                description = newTask.description,
                isCompleted = newTask.isCompleted.convertToBoolean(),
                createdAtTimestamp = newTask.createdAtTimestamp,
                id = newTask.id.toInt()
            )
        } else {
            //enqueue it to send later when connected to internet
        }
    }
}