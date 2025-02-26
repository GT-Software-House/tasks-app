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
    val completedAtTimestamp: String?,
    val createdAtTimestamp: String,
    val updatedAtTimestamp: String?
)

fun TaskNetworkModel.asTaskEntity() = TaskEntity(
    uuid = uuid,
    deviceId = deviceId,
    title = title,
    description = description,
    isCompleted = isCompleted,
    completedAtTimestamp = completedAtTimestamp,
    createdAtTimestamp = createdAtTimestamp,
    updatedAtTimestamp = updatedAtTimestamp
)