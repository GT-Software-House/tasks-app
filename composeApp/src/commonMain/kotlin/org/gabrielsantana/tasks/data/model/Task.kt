package org.gabrielsantana.tasks.data.model

import kotlinx.datetime.Instant
import org.gabrielsantana.tasks.TaskEntity
import org.gabrielsantana.tasks.data.source.remote.model.TaskNetworkModel

data class Task(
    val uuid: String,
    val deviceId: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val createdAt: Instant,
    val completedAt: Instant?,
    val updatedAt: Instant?
)

fun Task.asTaskEntity() = TaskEntity(
    uuid = uuid,
    deviceId = deviceId,
    title = title,
    description = description,
    isCompleted = isCompleted,
    completedAtTimestamp = completedAt?.toString(),
    createdAtTimestamp = createdAt.toString(),
    updatedAtTimestamp = updatedAt?.toString()
)

fun Task.asTaskNetworkModel() = TaskNetworkModel(
    uuid = uuid,
    deviceId = deviceId,
    title = title,
    description = description,
    isCompleted = isCompleted,
    completedAtTimestamp = completedAt?.toString(),
    createdAtTimestamp = createdAt.toString(),
    updatedAtTimestamp = updatedAt?.toString()
)
