package org.gabrielsantana.quicknotes.data.task.source.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
internal data class TaskTransaction(
    val id: Int,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("task_id")
    val taskUuid: Uuid,
    val operation: Operation,
)

internal enum class Operation {
    INSERT,
    UPDATE,
    DELETE
}
