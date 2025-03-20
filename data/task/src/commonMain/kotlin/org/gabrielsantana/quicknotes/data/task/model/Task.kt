package org.gabrielsantana.quicknotes.data.task.model

import kotlinx.datetime.Instant
import kotlin.uuid.Uuid

data class Task(
    val uuid: Uuid,
    val deviceId: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val createdAt: Instant,
    val completedAt: Instant?,
    val updatedAt: Instant
)