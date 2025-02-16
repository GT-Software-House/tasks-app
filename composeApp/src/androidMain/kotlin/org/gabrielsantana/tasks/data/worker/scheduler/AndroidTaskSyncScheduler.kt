package org.gabrielsantana.tasks.data.worker.scheduler

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.gabrielsantana.tasks.data.scheduler.QueueSyncStatus
import org.gabrielsantana.tasks.data.scheduler.TaskSyncScheduler
import org.gabrielsantana.tasks.data.worker.SyncTaskRemotelyWorker
import java.util.concurrent.TimeUnit

class AndroidTaskSyncScheduler(
    private val appContext: Context
) : TaskSyncScheduler {
    override suspend fun scheduleTask(taskId: Long) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val data = Data.Builder()
            .putLong("taskId", taskId)
            .build()
        val syncRequest = OneTimeWorkRequestBuilder<SyncTaskRemotelyWorker>()
            .setConstraints(constraints)
            .setInputData(data)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30,
                TimeUnit.MILLISECONDS
            )
            .build()
        WorkManager.getInstance(appContext)
            .enqueue(syncRequest)
    }

    override fun tasksWaitingSync(): Flow<QueueSyncStatus> =
        WorkManager
            .getInstance(appContext)
            .getWorkInfosFlow(WorkQuery.fromStates(WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING))
            .map { list ->
                when {
                    list.find { it.state == WorkInfo.State.RUNNING } != null -> QueueSyncStatus.Syncing
                    list.find { it.state == WorkInfo.State.ENQUEUED } != null -> QueueSyncStatus.Waiting
                    else -> QueueSyncStatus.Empty
                }
            }
}