package org.gabrielsantana.tasks.features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.gabrielsantana.tasks.Task
import org.gabrielsantana.tasks.data.TasksRepository

class HomeViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() = viewModelScope.launch {
        tasksRepository.getTasks().collect { value ->
            _uiState.update { state ->
                val newTasks = value.map { it.toUiModel() }
                state.copy(tasks = newTasks)
            }
        }
    }

}


fun Task.toUiModel(): TaskUiModel {
    return TaskUiModel(id.toInt(), title, description, isChecked > 0)
}
