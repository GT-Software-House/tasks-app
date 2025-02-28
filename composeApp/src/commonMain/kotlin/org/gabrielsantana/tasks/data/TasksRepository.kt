package org.gabrielsantana.tasks.data

import co.touchlab.kermit.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transformLatest
import kotlinx.datetime.Clock
import org.gabrielsantana.tasks.data.TasksRepository.Companion.TAG
import org.gabrielsantana.tasks.data.model.Task
import org.gabrielsantana.tasks.data.scheduler.TaskSyncScheduler
import org.gabrielsantana.tasks.data.source.local.TasksLocalDataSource
import org.gabrielsantana.tasks.data.source.local.model.asNetworkModel
import org.gabrielsantana.tasks.data.source.local.model.asTask
import org.gabrielsantana.tasks.data.source.remote.TasksRemoteDataSource
import org.gabrielsantana.tasks.data.source.remote.model.Operation
import org.gabrielsantana.tasks.data.source.remote.model.TaskTransaction
import org.gabrielsantana.tasks.features.settings.appearance.data.PreferencesRepository
import org.gabrielsantana.tasks.data.source.remote.model.asTaskEntity
import kotlin.collections.map

class TasksRepository(
    private val taskSyncScheduler: TaskSyncScheduler,
    private val localDataSource: TasksLocalDataSource,
    private val taskSyncer: TaskSyncer
) : Syncer by taskSyncer {
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
}

@OptIn(ExperimentalCoroutinesApi::class)
class TaskSyncer(
    private val remoteDataSource: TasksRemoteDataSource,
    private val localDataSource: TasksLocalDataSource,
    private val preferencesRepository: PreferencesRepository
) : Syncer, SyncerPreferences by preferencesRepository {

    companion object {
        private const val TAG = "TaskSyncer"
    }

    override suspend fun oneTimeSync() {
        val lastSync = getLastSync()
        val transactions = remoteDataSource.getTransactionHistoryAfter(lastSync)
        Logger.i(TAG) { "Starting manual sync from last sync: $lastSync" }
        fetchAndSaveChanges(transactions)
    }

    override suspend fun reactiveSync() {
        getLastSyncFlow()
            .flatMapLatest { lastSync ->
                Logger.i(TAG) { "Starting to listen remote changes from last sync: $lastSync" }
                remoteDataSource.getNewTransactions(lastSync)
            }.onEach { transactions ->
                fetchAndSaveChanges(transactions)
            }.onCompletion {
                Logger.i(TAG) { "Stopping to listen remote changes" }
            }
            .collect()
    }

    override suspend fun syncToRemote(taskUuid: String): Boolean {
        //here we can have a problem on finding the task or the task just has been deleted. How to diff the cases?
        val task = localDataSource.getById(taskUuid) ?: return true
        return remoteDataSource.upsert(task.asNetworkModel())
    }

    private suspend fun fetchAndSaveChanges(
        transactions: List<TaskTransaction>
    ) {
        if (transactions.isEmpty()) return
        updateLastSync(transactions.maxBy { it.createdAt }.createdAt)
        Logger.i(TAG) { "New remote changes: size: ${transactions.size}, values: $transactions" }
        transactions.groupBy { it.operation }.forEach {
            when (it.key) {
                Operation.INSERT, Operation.UPDATE -> {
                    addLocalTasks(it.value)
                }

                Operation.DELETE -> deleteLocalTasks(it.value)
            }
        }
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

}

interface Syncer {
    suspend fun oneTimeSync()
    suspend fun reactiveSync()

    /**
     * @return if the data was synced successfully
     */
    suspend fun syncToRemote(taskUuid: String): Boolean
}

interface SyncerPreferences {
    suspend fun getLastSync(): String?
    fun getLastSyncFlow(): Flow<String?>
    suspend fun updateLastSync(lastSync: String)
}