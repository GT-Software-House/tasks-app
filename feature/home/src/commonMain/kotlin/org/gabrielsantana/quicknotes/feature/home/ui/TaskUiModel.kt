package org.gabrielsantana.quicknotes.feature.home.ui

data class TaskUiModel(
    val uuid: String,
    val title: String,
    val description: String,
    val isChecked: Boolean
)