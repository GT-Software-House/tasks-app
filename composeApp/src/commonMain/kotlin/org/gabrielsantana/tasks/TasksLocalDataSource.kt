package org.gabrielsantana.tasks

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class TasksLocalDataSource(private val db: TasksDatabase) {
    private val queries = db.taskQueries

    // Set id = null to let SQLDelight autogenerate the id
    fun insert(id: Long?, descrption: String, isChecked: Boolean) {
        queries.insert(id = id, description = descrption, isChecked = isChecked.toLong())
    }

    // If you've added the coroutines extensions you'll be able to use asFlow()
    fun getAll() = queries.getAll().asFlow().mapToList(Dispatchers.IO)

    fun delete(id: Long) {
        queries.delete(id = id)
    }
}

private fun Boolean.toLong(): Long {
    return if (this) 1L else 0L
}
