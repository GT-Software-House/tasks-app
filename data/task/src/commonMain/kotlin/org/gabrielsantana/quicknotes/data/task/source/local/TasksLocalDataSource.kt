@file:OptIn(ExperimentalUuidApi::class)

package org.gabrielsantana.quicknotes.data.task.source.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.gabrielsantana.quicknotes.data.task.TaskDatabase
import org.gabrielsantana.quicknotes.data.task.TaskEntity
import org.gabrielsantana.quicknotes.data.task.getDeviceId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class TasksLocalDataSource(private val db: TaskDatabase) {
    private val queries = db.taskQueries

    /**
     * Create and return [org.gabrielsantana.quicknotes.TaskEntity]
     */
    fun insert(
        title: String,
        description: String,
        isCompleted: Boolean,
        createdAtTimestamp: Instant = Clock.System.now()
    ): TaskEntity {
        return queries.transactionWithResult {
            val uuid = Uuid.random()
            db.taskQueries.insert(
                uuid = uuid,
                deviceId = getDeviceId(),
                title = title,
                description = description,
                isCompleted = isCompleted,
                createdAtTimestamp = createdAtTimestamp,
                completedAtTimestamp = null,
                updatedAtTimestamp = createdAtTimestamp,
            )
            db.taskQueries.getById(uuid = uuid).executeAsOne()
        }

    }

    fun upsert(task: TaskEntity) {
        queries.insert(
            uuid = task.uuid,
            deviceId = task.deviceId,
            title = task.title,
            description = task.description,
            isCompleted = task.isCompleted,
            completedAtTimestamp = task.completedAtTimestamp,
            createdAtTimestamp = task.createdAtTimestamp,
            updatedAtTimestamp = task.updatedAtTimestamp

        )
    }

    fun getAll(): Flow<List<TaskEntity>> = queries.getAll().asFlow().mapToList(Dispatchers.IO)

    fun delete(uuid: Uuid) {
        queries.delete(uuid = uuid)
    }

    fun updateIsChecked(uuid: Uuid, isChecked: Boolean) {
        queries.updateIsChecked(
            isCompleted = isChecked,
            uuid = uuid,
            completedAtTimestamp = if (isChecked) Clock.System.now() else null
        )
    }

    fun updateTitleAndDescription(uuid: Uuid, title: String, description: String) {
        queries.updateTitleAndDescription(title = title, description = description, uuid = uuid)
    }

    /**
     * Retrieves a TaskEntity from the database by its ID.
     *
     * @param id The unique identifier of the TaskEntity to retrieve.
     * @return The TaskEntity with the specified ID, or null if no such entity exists.
     */
    fun getById(uuid: Uuid): TaskEntity? {
        return try {
            queries.getById(uuid = uuid).executeAsOne()
        } catch (_: NullPointerException) {
            null
        }
    }

}