package org.gabrielsantana.tasks.data

import co.touchlab.kermit.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import org.gabrielsantana.tasks.data.source.local.TasksLocalDataSource
import org.gabrielsantana.tasks.data.source.local.model.asNetworkModel
import org.gabrielsantana.tasks.data.source.remote.TasksRemoteDataSource
import org.gabrielsantana.tasks.data.source.remote.model.Operation
import org.gabrielsantana.tasks.data.source.remote.model.TaskTransaction
import org.gabrielsantana.tasks.data.source.remote.model.asTaskEntity
import org.gabrielsantana.tasks.features.settings.appearance.data.PreferencesRepository

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