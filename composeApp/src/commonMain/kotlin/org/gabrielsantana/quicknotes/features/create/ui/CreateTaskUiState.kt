package org.gabrielsantana.quicknotes.features.create.ui

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

data class CreateTaskUiState(
    val title: String = "",
    val taskUuidToEdit: Uuid? = null,
    val description: String = "",
    val isTitleInvalid: Boolean? = null,
    val isDescriptionInvalid: Boolean? = null,
    val taskAction: TaskAction? = null,
) {
    val isEditMode: Boolean
        get() = taskUuidToEdit != null
}

@Serializable
sealed class TaskAction {
    @Serializable
    data class Update(val taskUuid: Uuid) : TaskAction()
    @Serializable
    data object Create : TaskAction()
}