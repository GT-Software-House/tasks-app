package org.gabrielsantana.quicknotes.data.task.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import org.gabrielsantana.quicknotes.data.task.TasksRepository
import kotlin.uuid.Uuid

internal class TaskUpdateSyncerWorker(
    private val supabaseClient: SupabaseClient,
    private val tasksRepository: TasksRepository,
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val taskUuid = inputData.getString("taskUuid")
        if (taskUuid != null) {
            try {
                //need to load the session manually because it's cleaned on app close
                supabaseClient.auth.loadFromStorage()
                val syncedSuccessfully = tasksRepository.syncToRemote(Uuid.parse(taskUuid))
                if (!syncedSuccessfully) return Result.failure()
                return Result.success()
            } catch (e: Exception) {
                Log.e(TAG, "doWork failed at attempt $runAttemptCount}", e)
                return if (runAttemptCount == 3) {
                    Result.failure()
                } else {
                    Result.retry()
                }
            }
        } else {
            return Result.failure()
        }
    }
}

private const val TAG = "TaskUpdateSyncerWorker"