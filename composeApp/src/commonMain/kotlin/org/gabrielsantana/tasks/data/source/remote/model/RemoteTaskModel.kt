package org.gabrielsantana.tasks.data.source.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RemoteTaskModel(
    val id: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val completedAt: String?,
    val createdAt: String,
    val updatedAt: String?
)