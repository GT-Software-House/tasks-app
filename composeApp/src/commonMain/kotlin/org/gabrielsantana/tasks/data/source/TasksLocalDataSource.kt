package org.gabrielsantana.tasks.data.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.gabrielsantana.tasks.TasksDatabase

class TasksLocalDataSource(private val db: TasksDatabase) {
    private val queries = db.taskQueries

    // Set id = null to let SQLDelight autogenerate the id
    fun insert(title: String, description: String, isChecked: Boolean) {
        queries.insert(id = null, title = title, description = description, isChecked = isChecked.toLong())
    }

    // If you've added the coroutines extensions you'll be able to use asFlow()
    fun getAll() = queries.getAll().asFlow().mapToList(Dispatchers.IO)

    fun delete(id: Long) {
        queries.delete(id = id)
    }

    fun updateIsChecked(id: Long, isChecked: Boolean) {
        queries.updateIsChecked(isChecked = isChecked.toLong(), id = id)
    }


}

private fun Boolean.toLong(): Long {
    return if (this) 1L else 0L
}
