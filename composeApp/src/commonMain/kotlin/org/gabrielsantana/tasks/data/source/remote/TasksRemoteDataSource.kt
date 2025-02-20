package org.gabrielsantana.tasks.data.source.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Count
import kotlinx.datetime.Clock
import org.gabrielsantana.tasks.data.source.remote.model.RemoteTaskModel

class TasksRemoteDataSource(
    private val supabaseClient: SupabaseClient
) {

    companion object {
        private const val TABLE_NAME_TASKS = "tasks"
    }

    suspend fun insert(
        uuid: String,
        deviceId: String,
        title: String,
        description: String,
        isCompleted: Boolean,
        createdAtTimestamp: Long = Clock.System.now().toEpochMilliseconds(),
    ) {
        val task = RemoteTaskModel(
            uuid = uuid,
            deviceId = deviceId,
            title = title,
            description = description,
            isCompleted = isCompleted,
            completedAt = null,
            updatedAt = null,
            createdAt = createdAtTimestamp.toString(),
        )
        supabaseClient.from(TABLE_NAME_TASKS).insert(task)
    }

    suspend fun upsert(
        task: RemoteTaskModel,
    ): Boolean {
        val count = supabaseClient.from(TABLE_NAME_TASKS).upsert(task) {
            count(Count.EXACT)
        }.countOrNull() ?: 0L
        return count > 0L
    }

    suspend fun getAll(): List<RemoteTaskModel> {
        return supabaseClient.from(TABLE_NAME_TASKS).select().decodeList()
    }


}