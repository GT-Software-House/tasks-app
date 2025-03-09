package org.gabrielsantana.tasks.home.ui

data class TaskUiModel(
    val uuid: String,
    val title: String,
    val description: String,
    val isChecked: Boolean
)