package org.gabrielsantana.tasks.data

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.takeWhile
import kotlinx.datetime.Clock
import org.gabrielsantana.tasks.data.model.Task
import org.gabrielsantana.tasks.data.scheduler.TaskSyncScheduler
import org.gabrielsantana.tasks.data.source.local.TasksLocalDataSource
import org.gabrielsantana.tasks.data.source.local.model.asRemoteTask
import org.gabrielsantana.tasks.data.source.local.model.asTask
import org.gabrielsantana.tasks.data.source.remote.TasksRemoteDataSource
import org.gabrielsantana.tasks.data.source.remote.model.Operation
import org.gabrielsantana.tasks.data.source.remote.model.TaskTransaction
import org.gabrielsantana.tasks.features.settings.appearance.data.PreferencesRepository
import org.gabrielsantana.tasks.data.source.remote.model.asTaskEntity

class TasksRepository(
    private val taskSyncScheduler: TaskSyncScheduler,
    private val localDataSource: TasksLocalDataSource,
    private val remoteDataSource: TasksRemoteDataSource,
    private val preferencesRepository: PreferencesRepository
) {
    companion object {
        private const val TAG = "TasksRepository"
    }

    fun getTasks(): Flow<List<Task>> = localDataSource.getAll().map {
        it.map { entity -> entity.asTask() }.sortedBy { it.createdAt.epochSeconds }
    }

    fun deleteTask(uuid: String) {
        localDataSource.delete(uuid)
        taskSyncScheduler.scheduleDelete(uuid)
    }

    fun getTaskById(uuid: String): Task? = localDataSource.getById(uuid)?.asTask()



    fun updateTask(uuid: String, isChecked: Boolean) {
        localDataSource.updateIsChecked(uuid, isChecked)
        taskSyncScheduler.scheduleTaskUpdate(uuid)
    }

    fun updateTaskTitleAndDescription(uuid: String, title: String, description: String) {
        localDataSource.updateTitleAndDescription(uuid, title, description)
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
        return remoteDataSource.upsert(task.asRemoteTask())
    }

    suspend fun sync() {
        val lastSync = preferencesRepository.getLastSync()
        val transactions = remoteDataSource.getTransactionHistoryAfter(lastSync)
        saveTransactionHistory(transactions)
    }

    private suspend fun saveTransactionHistory(transactionHistory: List<TaskTransaction>) {
        transactionHistory.groupBy { it.operation }.forEach {
            when (it.key) {
                Operation.INSERT, Operation.UPDATE -> {
                    addLocalTasks(it.value)
                }

                Operation.DELETE -> deleteLocalTasks(it.value)
            }
        }
        preferencesRepository.updateLastSync(Clock.System.now().toString())
    }

    private suspend fun addLocalTasks(transactions: List<TaskTransaction>) {
        val uuids = transactions.map { it.taskUuid }
        remoteDataSource.getByIds(uuids).map {
            localDataSource.upsert(it.asTaskEntity())
        }
    }

    private suspend fun deleteLocalTasks(taskUuids: List<TaskTransaction>) {
        taskUuids.forEach {
            localDataSource.delete(it.taskUuid)
        }
    }

    suspend fun listenRemoteChanges() {
        Logger.i(TAG) { "Starting to listen remote changes" }
        val lastSync = preferencesRepository.getLastSync()
        remoteDataSource.getNewTransactions(lastSync).collect { transactions ->
            if (transactions.isEmpty()) return@collect
            Logger.i(TAG) { "New remote changes: size${transactions.size}, values: $transactions" }
            saveTransactionHistory(transactions)
        }
        Logger.i(TAG) { "Stopping to listen remote changes" }
    }

}