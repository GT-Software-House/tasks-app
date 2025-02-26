package org.gabrielsantana.tasks.data.source.remote.model

import kotlinx.serialization.Serializable
import org.gabrielsantana.tasks.TaskEntity

@Serializable
data class TaskNetworkModel(
    val uuid: String,
    val deviceId: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val completedAt: String?,
    val createdAt: String,
    val updatedAt: String?
)

fun TaskNetworkModel.asTaskEntity() = TaskEntity(
    uuid = uuid,
    deviceId = deviceId,
    title = title,
    description = description,
    isCompleted = isCompleted,
    completedAtTimestamp = completedAt,
    createdAtTimestamp = createdAt,
    updatedAtTimestamp = updatedAt
)