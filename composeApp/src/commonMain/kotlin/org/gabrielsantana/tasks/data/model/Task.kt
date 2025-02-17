package org.gabrielsantana.tasks.data.model

import kotlinx.datetime.Instant

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