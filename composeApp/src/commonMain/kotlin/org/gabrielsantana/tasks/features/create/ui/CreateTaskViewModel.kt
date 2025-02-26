package org.gabrielsantana.tasks.features.create.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.gabrielsantana.tasks.data.TasksRepository
import org.gabrielsantana.tasks.ui.AppScreens

class CreateTaskViewModel(
    savedStateHandle: SavedStateHandle,
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState = _uiState.asStateFlow()

    init {
        switchToEditModeIfNeeded(savedStateHandle)
    }

    private fun switchToEditModeIfNeeded(savedStateHandle: SavedStateHandle) {
        viewModelScope.launch {
            val route = savedStateHandle.toRoute<AppScreens.CreateTask>()
            if (route.taskUuid == null) return@launch
            val task = tasksRepository.getTaskById(route.taskUuid)
            if (task == null) return@launch

            _uiState.update {
                it.copy(
                    taskUuidToEdit = task.uuid,
                    title = task.title,
                    description = task.description
                )
            }
        }
    }

    fun updateTitle(newTitle: String) {
        _uiState.update { it.copy(title = newTitle, isTitleInvalid = null) }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update { it.copy(description = newDescription, isDescriptionInvalid = null) }
    }

    fun createTask() {
        with(uiState.value) {
            viewModelScope.launch {
                if (!validateFields()) return@launch
                if (taskUuidToEdit != null) {
                    tasksRepository.updateTaskTitleAndDescription(
                        uuid = taskUuidToEdit,
                        title = title,
                        description = description
                    )
                    _uiState.update { it.copy(taskAction = TaskAction.Update(taskUuidToEdit)) }
                } else {
                    tasksRepository.createTask(title, description)
                    _uiState.update { it.copy(taskAction = TaskAction.Create) }
                }

            }
        }
    }

    private fun validateFields(): Boolean = with(_uiState.value) {
        if (title.isEmpty()) {
            _uiState.update { it.copy(isTitleInvalid = true) }
        }
        if (description.isEmpty()) {
            _uiState.update { it.copy(isDescriptionInvalid = true) }
        }
        return title.isNotEmpty() && description.isNotEmpty()
    }

}