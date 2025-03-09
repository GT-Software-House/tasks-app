package org.gabrielsantana.quicknotes.home.ui

data class TaskUiModel(
    val uuid: String,
    val title: String,
    val description: String,
    val isChecked: Boolean
)