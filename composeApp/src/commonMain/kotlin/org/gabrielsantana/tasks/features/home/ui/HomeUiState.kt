package org.gabrielsantana.tasks.features.home.ui

data class HomeUiState(
    val tasks: List<TaskUiModel> = emptyList()
)

data class TaskUiModel(
    val id: Int,
    val title: String,
    val description: String,
    val isChecked: Boolean
)
