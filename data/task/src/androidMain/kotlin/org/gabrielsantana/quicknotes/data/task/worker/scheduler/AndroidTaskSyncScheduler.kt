package org.gabrielsantana.quicknotes.data.task.worker.scheduler

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import org.gabrielsantana.quicknotes.data.task.scheduler.TaskSyncScheduler
import org.gabrielsantana.quicknotes.data.task.worker.SyncTaskRemotelyWorker
import org.gabrielsantana.quicknotes.data.task.worker.TaskDeleteSyncerWorker
import org.gabrielsantana.quicknotes.data.task.worker.TaskUpdateSyncerWorker
import java.util.concurrent.TimeUnit
import kotlin.uuid.Uuid

internal class AndroidTaskSyncScheduler(
    private val appContext: Context
) : TaskSyncScheduler {

    private val workManager: WorkManager
        get() = WorkManager.getInstance(appContext)

    override fun scheduleTask(taskUuid: Uuid) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val data = Data.Builder()
            .putString("taskUuid", taskUuid.toString())
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
        workManager.enqueue(syncRequest)
    }

    override fun scheduleDelete(taskUuid: Uuid) {
        val taskUuidString = taskUuid.toString()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val data = Data.Builder()
            .putString("taskUuid", taskUuidString)
            .build()
        val syncRequest = OneTimeWorkRequestBuilder<TaskDeleteSyncerWorker>()
            .setConstraints(constraints)
            .setInputData(data)
            .addTag(buildTag(taskUuidString))
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30,
                TimeUnit.MILLISECONDS
            )
            .build()
        workManager.enqueue(syncRequest)
    }

    override fun scheduleTaskUpdate(taskUuid: Uuid) {
        //If we have a scheduled update for determinate task, we don't need to schedule another
        val taskUuidString = taskUuid.toString()
        if (alreadyHaveWorksForTask(taskUuidString)) {
            return
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val data = Data.Builder()
            .putString("taskUuid", taskUuidString)
            .build()
        val syncRequest = OneTimeWorkRequestBuilder<TaskUpdateSyncerWorker>()
            .setConstraints(constraints)
            .setInputData(data)
            .addTag(buildTag(taskUuidString))
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30,
                TimeUnit.MILLISECONDS
            )
            .build()
        workManager.enqueue(syncRequest)
    }

    private fun alreadyHaveWorksForTask(uuid: String): Boolean {
        return workManager.getWorkInfosByTag(buildTag(uuid)).get().find {
            it.state == WorkInfo.State.ENQUEUED
        } != null
    }

    private fun buildTag(taskUuid: String): String {
        return "TaskUpdateSyncerWorker={id:$taskUuid}"
    }

//    override fun tasksWaitingSync(): Flow<QueueSyncStatus> =
//        WorkManager
//            .getInstance(appContext)
//            .getWorkInfosFlow(WorkQuery.fromStates(WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING))
//            .map { list ->
//                when {
//                    list.find { it.state == WorkInfo.State.RUNNING } != null -> QueueSyncStatus.Syncing
//                    list.find { it.state == WorkInfo.State.ENQUEUED } != null -> QueueSyncStatus.Waiting
//                    else -> QueueSyncStatus.Empty
//                }
//            }
}