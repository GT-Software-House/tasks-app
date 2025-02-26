@file:OptIn(ExperimentalUuidApi::class)

package org.gabrielsantana.tasks.data.source.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import org.gabrielsantana.tasks.TaskEntity
import org.gabrielsantana.tasks.TasksDatabase
import org.gabrielsantana.tasks.getDeviceId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
            val uuid = Uuid.random().toString()
            db.taskQueries.insert(
                uuid = uuid,
                deviceId = getDeviceId(),
                title = title,
                description = description,
                isCompleted = isCompleted,
                createdAtTimestamp = createdAtTimestamp,
                completedAtTimestamp = null,
                updatedAtTimestamp = null,
            )
            db.taskQueries.getById(uuid = uuid).executeAsOne()
        }

    }

    fun getAll(): Flow<List<TaskEntity>> = queries.getAll().asFlow().mapToList(Dispatchers.IO)

    fun delete(uuid: String) {
        queries.delete(uuid = uuid)
    }

    fun updateIsChecked(uuid: String, isChecked: Boolean) {
        queries.updateIsChecked(isCompleted = isChecked, uuid = uuid)
    }

    fun updateTitleAndDescription(uuid: String, title: String, description: String) {
        queries.updateTitleAndDescription(title = title, description = description, uuid = uuid)
    }

    /**
     * Retrieves a TaskEntity from the database by its ID.
     *
     * @param id The unique identifier of the TaskEntity to retrieve.
     * @return The TaskEntity with the specified ID, or null if no such entity exists.
     */
    fun getById(uuid: String): TaskEntity? {
        return try {
            queries.getById(uuid = uuid).executeAsOne()
        } catch (_: NullPointerException) {
            null
        }
    }

}