package org.gabrielsantana.quicknotes.data.task.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import org.gabrielsantana.quicknotes.data.task.source.local.TasksLocalDataSource
import org.gabrielsantana.quicknotes.data.task.source.local.model.asNetworkModel
import org.gabrielsantana.quicknotes.data.task.source.remote.TasksRemoteDataSource
import kotlin.uuid.Uuid

internal class SyncTaskRemotelyWorker(
    private val supabaseClient: SupabaseClient,
    private val tasksRemoteDataSource: TasksRemoteDataSource,
    private val tasksLocalDataSource: TasksLocalDataSource,
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val taskId = inputData.getString("taskUuid")
        if (taskId != null) {
            try {
                //here we can have a problem on finding the task or the task just has been deleted. How to diff the cases?
                val task = tasksLocalDataSource.getById(Uuid.parse(taskId)) ?: return Result.success()
                //need to load the session manually because it's cleaned on app close
                supabaseClient.auth.loadFromStorage()
                tasksRemoteDataSource.insert(task.asNetworkModel())
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

private const val TAG = "SyncTaskRemotelyWorker"