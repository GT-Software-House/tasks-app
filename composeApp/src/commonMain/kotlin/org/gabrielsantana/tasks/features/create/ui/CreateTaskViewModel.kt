package org.gabrielsantana.tasks.features.create.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.gabrielsantana.tasks.data.TasksRepository

class CreateTaskViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState = _uiState.asStateFlow()

    fun updateTitle(newTitle: String) {
        _uiState.update { it.copy(title = newTitle, isTitleInvalid = null) }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update { it.copy(description = newDescription, isDescriptionInvalid = null) }
    }

    fun createTask() {
        if (uiState.value.title.isEmpty()) {
            _uiState.update { it.copy(isTitleInvalid = true) }
        }
        if (uiState.value.description.isEmpty()) {
            _uiState.update { it.copy(isDescriptionInvalid = true) }
        }
        if (uiState.value.title.isNotEmpty() && uiState.value.description.isNotEmpty()) {
            tasksRepository.createTask(uiState.value.title, uiState.value.description)
            _uiState.update { it.copy(taskCreatedSuccessfully = true) }
        }
    }

}