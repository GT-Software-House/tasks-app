package org.gabrielsantana.tasks.features.create.ui

data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val isTitleInvalid: Boolean? = null,
    val isDescriptionInvalid: Boolean? = null,
    val taskCreatedSuccessfully: Boolean = false,
)