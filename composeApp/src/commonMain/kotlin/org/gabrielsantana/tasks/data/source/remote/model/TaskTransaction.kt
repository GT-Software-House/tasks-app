package org.gabrielsantana.tasks.data.source.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskTransaction(
    val id: Int,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("task_id")
    val taskUuid: String,
    val operation: Operation,
)

enum class Operation {
    INSERT,
    UPDATE,
    DELETE
}
