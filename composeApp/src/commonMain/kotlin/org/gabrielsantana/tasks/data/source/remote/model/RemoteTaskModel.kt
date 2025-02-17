package org.gabrielsantana.tasks.data.source.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RemoteTaskModel(
    val uuid: String,
    val deviceId: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val completedAt: String?,
    val createdAt: String,
    val updatedAt: String?
)