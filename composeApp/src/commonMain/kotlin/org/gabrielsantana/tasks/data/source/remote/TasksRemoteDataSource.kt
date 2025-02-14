package org.gabrielsantana.tasks.data.source.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.datetime.Clock
import org.gabrielsantana.tasks.data.source.remote.model.RemoteTaskModel

class TasksRemoteDataSource(
    private val supabaseClient: SupabaseClient
) {

    suspend fun insert(
        id: Int,
        title: String,
        description: String,
        isCompleted: Boolean,
        createdAtTimestamp: Long = Clock.System.now().toEpochMilliseconds(),
    ) {
        supabaseClient.auth.currentUserOrNull()?.id
        val task = RemoteTaskModel(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted,
            completedAt = createdAtTimestamp.toString(),
            updatedAt = null,
            createdAt = createdAtTimestamp.toString(),
        )
        supabaseClient.from("tasks").insert(task)
    }

}