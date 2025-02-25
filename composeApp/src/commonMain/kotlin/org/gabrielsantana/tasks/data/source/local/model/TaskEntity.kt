package org.gabrielsantana.tasks.data.source.local.model

import kotlinx.datetime.Instant
import org.gabrielsantana.tasks.TaskEntity
import org.gabrielsantana.tasks.data.model.Task
import org.gabrielsantana.tasks.data.source.remote.model.RemoteTaskModel

//The model is auto generated by the SQLDelight, this file is just for map function

fun TaskEntity.asTask(): Task = Task(
    uuid = uuid,
    deviceId = deviceId,
    title = title,
    description = description,
    isCompleted = isCompleted,
    createdAt = Instant.parse(createdAtTimestamp),
    completedAt = completedAtTimestamp?.let { Instant.parse(it) },
    updatedAt = updatedAtTimestamp?.let { Instant.parse(it) }
)

fun TaskEntity.asRemoteTask() = RemoteTaskModel(
    uuid = uuid,
    deviceId = deviceId,
    title = title,
    description = description,
    isCompleted = isCompleted,
    completedAt = completedAtTimestamp?.toString(),
    createdAt = createdAtTimestamp.toString(),
    updatedAt = updatedAtTimestamp?.toString()
)