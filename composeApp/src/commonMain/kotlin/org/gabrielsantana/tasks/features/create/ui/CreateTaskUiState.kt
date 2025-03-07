package org.gabrielsantana.tasks.features.create.ui

import kotlinx.serialization.Serializable

data class CreateTaskUiState(
    val title: String = "",
    val taskUuidToEdit: String? = null,
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
    data class Update(val taskUuid: String) : TaskAction()
    @Serializable
    data object Create : TaskAction()
}