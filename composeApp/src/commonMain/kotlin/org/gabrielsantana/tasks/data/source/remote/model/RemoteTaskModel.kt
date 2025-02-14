package org.gabrielsantana.tasks.data.source.remote.model

import kotlinx.serialization.Serializable
import org.gabrielsantana.tasks.TaskEntity
import org.gabrielsantana.tasks.data.convertToNumber

@Serializable
data class RemoteTaskModel(
    val id: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val completedAt: String?,
    val createdAt: String,
    val updatedAt: String?
)

fun RemoteTaskModel.asTaskEntity(): TaskEntity = TaskEntity(
    id = id.toLong(),
    title = title,
    description = description,
    isCompleted = isCompleted.convertToNumber().toLong(),
    completedAtTimestamp = completedAt?.toLong(),
    createdAtTimestamp = createdAt.toLong(),
    updatedAtTimestamp = updatedAt?.toLong()
)