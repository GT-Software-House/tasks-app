package org.gabrielsantana.tasks.data

import org.gabrielsantana.tasks.data.source.TasksLocalDataSource


class TasksRepository(private val tasksDataSource: TasksLocalDataSource) {

    fun getTasks() = tasksDataSource.getAll()

    fun deleteTask(id: Long) = tasksDataSource.delete(id)

    fun updateTask(id: Long, isChecked: Boolean) = tasksDataSource.updateIsChecked(id, isChecked)

    fun createTask(title: String, description: String, isChecked: Boolean = false) =
        tasksDataSource.insert(title, description, isChecked)

}