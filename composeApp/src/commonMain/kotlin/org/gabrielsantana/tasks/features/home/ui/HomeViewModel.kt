package org.gabrielsantana.tasks.features.home.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.gabrielsantana.tasks.Task
import org.gabrielsantana.tasks.data.TasksRepository

class HomeViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun selectTask(taskIndex: Int) = _uiState.update { state ->
        if (state.selectedTasksIndex.contains(taskIndex)) {
            state.copy(selectedTasksIndex = state.selectedTasksIndex.toMutableSet().apply { remove(taskIndex) })
        } else {
            state.copy(selectedTasksIndex = state.selectedTasksIndex.toMutableSet().apply { add(taskIndex) })
        }
    }

    fun clearSelectedTasks() = _uiState.update { state ->
        state.copy(selectedTasksIndex = emptySet())
    }

    fun deleteSelectedTasks(): Unit = with(_uiState.value) {
        selectedTasksIndex.forEach { taskIndex ->
            tasksRepository.deleteTask(tasks[taskIndex].id.toLong())
        }
        clearSelectedTasks()
    }

    fun selectTaskFilter(newFilter: TaskFilter) {
        viewModelScope.launch {
            val tasks = tasksRepository.getTasks().first()
            _uiState.update {
                it.copy(
                    selectedTaskFilter = newFilter,
                    tasks = tasks.filter { newFilter.predicate(it) }.map { it.toUiModel() })
            }
        }
    }

    fun loadTasks() = viewModelScope.launch {
        tasksRepository.getTasks().collect { value ->
            _uiState.update { state ->
                val newTasks = value
                    .filter {
                        _uiState.value.selectedTaskFilter.predicate(it)
                    }.map {
                        it.toUiModel()
                    }
                state.copy(tasks = newTasks)
            }
        }
    }

    fun updateTask(isChecked: Boolean, task: TaskUiModel) {
        viewModelScope.launch {
            tasksRepository.updateTask(task.id.toLong(), isChecked)
        }
    }

}


fun Task.toUiModel(): TaskUiModel {
    return TaskUiModel(id.toInt(), title, description, isChecked > 0)
}
