package org.gabrielsantana.tasks.data.source.remote.model

import kotlinx.serialization.Serializable
import org.gabrielsantana.tasks.TaskEntity

@Serializable
data class RemoteTaskModel(
    val uuid: String,
    val deviceId: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val completedAt: String?,
    val createdAt: String,
    val updatedAt: String?
)

fun RemoteTaskModel.asTaskEntity() = TaskEntity(
    uuid = uuid,
    deviceId = deviceId,
    title = title,
    description = description,
    isCompleted = isCompleted,
    completedAtTimestamp = completedAt?.toLong(),
    createdAtTimestamp = createdAt.toLong(),
    updatedAtTimestamp = updatedAt?.toLong()
)