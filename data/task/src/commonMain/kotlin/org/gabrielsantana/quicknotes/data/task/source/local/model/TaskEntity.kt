package org.gabrielsantana.quicknotes.data.task.source.local.model

import kotlinx.datetime.Instant
import org.gabrielsantana.quicknotes.data.task.TaskEntity
import org.gabrielsantana.quicknotes.data.task.model.Task
import org.gabrielsantana.quicknotes.data.task.source.remote.model.TaskNetworkModel

//The model is auto generated by the SQLDelight, this file is just for map function

internal fun TaskEntity.asTask(): Task = Task(
    uuid = uuid,
    deviceId = deviceId,
    title = title,
    description = description,
    isCompleted = isCompleted,
    createdAt = createdAtTimestamp,
    completedAt = completedAtTimestamp,
    updatedAt = updatedAtTimestamp
)

internal fun TaskEntity.asNetworkModel() = TaskNetworkModel(
    uuid = uuid,
    deviceId = deviceId,
    title = title,
    description = description,
    isCompleted = isCompleted,
    completedAt = completedAtTimestamp,
    createdAt = createdAtTimestamp,
    updatedAt = updatedAtTimestamp
)