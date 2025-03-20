@file:OptIn(ExperimentalUuidApi::class)

package org.gabrielsantana.quicknotes.feature.home.ui

import org.gabrielsantana.quicknotes.data.task.model.Task
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class TaskUiModel(
    val uuid: Uuid,
    val title: String,
    val description: String,
    val isChecked: Boolean
)

fun Task.toUiModel(): TaskUiModel {
    return TaskUiModel(uuid, title, description, isCompleted)
}
