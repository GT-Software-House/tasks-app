package org.gabrielsantana.quicknotes.data.task.source.remote.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gabrielsantana.quicknotes.data.task.TaskEntity
import kotlin.uuid.Uuid

@Serializable
internal data class TaskNetworkModel(
    @SerialName("uuid")
    val uuid: Uuid,
    @SerialName("device_id")
    val deviceId: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("is_completed")
    val isCompleted: Boolean,
    @SerialName("completed_at")
    val completedAt: Instant?,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("updated_at")
    val updatedAt: Instant
)

internal fun TaskNetworkModel.asTaskEntity() = TaskEntity(
    uuid = uuid,
    deviceId = deviceId,
    title = title,
    description = description,
    isCompleted = isCompleted,
    completedAtTimestamp = completedAt,
    createdAtTimestamp = createdAt,
    updatedAtTimestamp = updatedAt
)