@file:OptIn(ExperimentalStoreApi::class)

package org.gabrielsantana.tasks

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.map
import org.gabrielsantana.tasks.data.model.Task
import org.gabrielsantana.tasks.data.model.asTaskEntity
import org.gabrielsantana.tasks.data.model.asTaskNetworkModel
import org.gabrielsantana.tasks.data.source.local.TasksLocalDataSource
import org.gabrielsantana.tasks.data.source.local.model.asTask
import org.gabrielsantana.tasks.data.source.remote.TasksRemoteDataSource
import org.gabrielsantana.tasks.data.source.remote.model.TaskNetworkModel
import org.gabrielsantana.tasks.data.source.remote.model.asTaskEntity
import org.mobilenativefoundation.store.store5.Bookkeeper
import org.mobilenativefoundation.store.store5.Converter
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MutableStore
import org.mobilenativefoundation.store.store5.MutableStoreBuilder
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Updater
import org.mobilenativefoundation.store.store5.UpdaterResult

typealias TaskStore = MutableStore<String, Task>

class TasksStoreFactory(
    private val tasksLocalDataSource: TasksLocalDataSource,
    private val tasksRemoteDataSource: TasksRemoteDataSource,
    private val tasksDatabase: TasksDatabase,
) {

    companion object {
        private const val TAG = "TaskStore"
    }

    fun create(): TaskStore {
        return MutableStoreBuilder.from(
            fetcher = createFetcher(),
            sourceOfTruth = createSourceOfTruth(),
            converter = createConverter()
        ).build(
            updater = createUpdater(),
            bookkeeper = createBookkeeper()
        )
    }

    private fun createFetcher(): Fetcher<String, TaskNetworkModel> {
        return Fetcher.of { id ->
            tasksRemoteDataSource.getByIds(listOf(id)).firstOrNull()
                ?: throw IllegalStateException("Task with id $id not found")
        }
    }

    private fun createSourceOfTruth(): SourceOfTruth<String, TaskEntity, Task> {
        return SourceOfTruth.of(
            reader = { id ->
                tasksLocalDataSource.getByIdAsFlow(id).map { it?.asTask() }
            },
            writer = { _, taskEntity ->
                tasksLocalDataSource.upsert(taskEntity)
            }
        )
    }

    private fun createConverter(): Converter<TaskNetworkModel, TaskEntity, Task> {
        return Converter.Builder<TaskNetworkModel, TaskEntity, Task>()
            .fromOutputToLocal { task -> task.asTaskEntity() }
            .fromNetworkToLocal { taskNetworkModel -> taskNetworkModel.asTaskEntity() }
            .build()
    }

    private fun createUpdater(): Updater<String, Task, Boolean> {
        return Updater.by(
            post = { _, updatedTask ->
                val networkModel = updatedTask.asTaskNetworkModel()
                val success = tasksRemoteDataSource.upsert(networkModel)
                if (success) {
                    UpdaterResult.Success.Typed(success)
                } else {
                    UpdaterResult.Error.Message("Something went wrong.")
                }
            }
        )
    }

    private fun createBookkeeper(): Bookkeeper<String> =
        Bookkeeper.by(
            getLastFailedSync = { id ->
                //TODO: improve this getting the most recent failed sync directly from database
                val lastSync = tasksDatabase.failedSyncQueries.getAll().executeAsList().last()
                lastSync.timestamp.toLong()
            },
            setLastFailedSync = { id, timestamp ->
                try {
                    tasksDatabase.failedSyncQueries.insert(id = null, taskId = id, timestamp = timestamp.toString())
                    true
                } catch (e: Exception) {
                    // Handle the exception
                    Logger.e(TAG) { "Failed to set last failed sync: $e" }
                    false
                }
            },
            clear = { id ->
                try {
                    tasksLocalDataSource.deleteById(id)
                    true
                } catch (e: Exception) {
                    // Handle the exception
                    Logger.e(TAG) { "Failed to clear last failed sync: $e" }
                    false
                }
            },
            clearAll = {
                try {
                    tasksLocalDataSource.deleteAll()
                    true
                } catch (e: Exception) {
                    // Handle the exception
                    Logger.e(TAG) { "Failed to clear all last failed sync: $e" }
                    false
                }
            }
        )

}