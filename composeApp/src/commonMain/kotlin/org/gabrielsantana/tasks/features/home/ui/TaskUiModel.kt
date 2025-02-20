package org.gabrielsantana.tasks.features.home.ui

data class TaskUiModel(
    val uuid: String,
    val title: String,
    val description: String,
    val isChecked: Boolean
)