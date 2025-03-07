package org.gabrielsantana.tasks.data.source.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gabrielsantana.tasks.TaskEntity

@Serializable
data class TaskNetworkModel(
    @SerialName("uuid")
    val uuid: String,
    @SerialName("device_id")
    val deviceId: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("is_completed")
    val isCompleted: Boolean,
    @SerialName("completed_at")
    val completedAt: String?,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
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