package org.gabrielsantana.tasks.data.source.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import org.gabrielsantana.tasks.TaskEntity
import org.gabrielsantana.tasks.TasksDatabase

class TasksLocalDataSource(private val db: TasksDatabase) {
    private val queries = db.taskQueries

    // Set id = null to let SQLDelight autogenerate the id
    /**
     * Create and return [TaskEntity]
     */
    fun insert(
        title: String,
        description: String,
        isCompleted: Boolean,
        createdAtTimestamp: Long = Clock.System.now().toEpochMilliseconds(),
    ): TaskEntity {
        return queries.transactionWithResult {
            db.taskQueries.insert(
                id = null,
                title = title,
                description = description,
                isCompleted = isCompleted.toLong(),
                createdAtTimestamp = createdAtTimestamp,
                completedAtTimestamp = null,
                updatedAtTimestamp = null
            )
            val id = db.taskQueries.selectLastInsertRowId().executeAsOne()
            db.taskQueries.getById(id).executeAsOne()
        }

    }

    fun getAll(): Flow<List<TaskEntity>> = queries.getAll().asFlow().mapToList(Dispatchers.IO)

    fun delete(id: Long) {
        queries.delete(id = id)
    }

    fun updateIsChecked(id: Long, isChecked: Boolean) {
        queries.updateIsChecked(isCompleted = isChecked.toLong(), id = id)
    }

    /**
     * Retrieves a TaskEntity from the database by its ID.
     *
     * @param id The unique identifier of the TaskEntity to retrieve.
     * @return The TaskEntity with the specified ID, or null if no such entity exists.
     */
    fun getById(id: Long): TaskEntity? {
        return try {
            queries.getById(id = id).executeAsOne()
        } catch (_: NullPointerException) {
            null
        }
    }

}

private fun Boolean.toLong(): Long {
    return if (this) 1L else 0L
}