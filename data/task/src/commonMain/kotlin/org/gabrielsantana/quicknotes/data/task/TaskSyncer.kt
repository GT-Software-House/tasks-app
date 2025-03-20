package org.gabrielsantana.quicknotes.data.task

import co.touchlab.kermit.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.gabrielsantana.quicknotes.data.task.source.local.TasksLocalDataSource
import org.gabrielsantana.quicknotes.data.task.source.local.model.asNetworkModel
import org.gabrielsantana.quicknotes.data.task.source.remote.TasksRemoteDataSource
import org.gabrielsantana.quicknotes.data.task.source.remote.model.Operation
import org.gabrielsantana.quicknotes.data.task.source.remote.model.TaskTransaction
import org.gabrielsantana.quicknotes.data.task.source.remote.model.asTaskEntity
import kotlin.collections.map
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
internal class TaskSyncer(
    private val remoteDataSource: TasksRemoteDataSource,
    private val localDataSource: TasksLocalDataSource,
    private val syncerPreferences: SyncerPreferences
) : Syncer, SyncerPreferences by syncerPreferences {

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
            }.catch {
                Logger.e(TAG) { "Error on realtime change listening" }
            }
            .collect()
    }

    override suspend fun syncToRemote(taskUuid: Uuid): Boolean {
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

    private fun deleteLocalTasks(taskUuids: List<TaskTransaction>) {
        taskUuids.forEach {
            localDataSource.delete(it.taskUuid)
        }
    }

}