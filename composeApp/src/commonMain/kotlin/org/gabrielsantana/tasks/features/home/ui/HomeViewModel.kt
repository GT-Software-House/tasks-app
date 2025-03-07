package org.gabrielsantana.tasks.features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.gabrielsantana.tasks.data.TasksRepository
import org.gabrielsantana.tasks.data.model.Task
import org.gabrielsantana.tasks.data.scheduler.TaskSyncScheduler
import org.gabrielsantana.tasks.features.settings.TaskFilter
import kotlin.time.Duration.Companion.milliseconds

class HomeViewModel(
    private val tasksRepository: TasksRepository,
    private val scheduler: TaskSyncScheduler
) : ViewModel() {

    private var getTasksJob: Job? = null

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTasks()
        //TODO: collect this in another place?
        viewModelScope.launch {
            scheduler.tasksWaitingSync().collect { syncStatus ->
                _uiState.update {
                    it.copy(syncStatus = syncStatus)
                }
            }
        }
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
            tasksRepository.deleteTask(tasks[taskIndex].uuid)
        }
        clearSelectedTasks()
    }

    //Used by iOS native UI
    @Suppress("unused")
    fun deleteTask(task: TaskUiModel) {
        tasksRepository.deleteTask(task.uuid)
    }

    //Used by iOS native UI
    @Suppress("unused")
    fun searchTasks(query: String) {
        getTasksJob?.cancel()
        getTasksJob = viewModelScope.launch {
            delay(300.milliseconds)
            if (query.isNotEmpty()) {
                tasksRepository.getTasks().collect { tasks ->
                    val filteredTasks = tasks.filter { task ->
                        task.title.contains(query, ignoreCase = true)
                    }.map { it.toUiModel() }
                    _uiState.update {
                        it.copy(tasks = filteredTasks)
                    }
                }
            } else {
                loadTasks()
            }
        }

    }

    fun selectTaskFilter(newFilter: TaskFilter) {
        _uiState.update { state ->
            state.copy(selectedTaskFilter = newFilter)
        }
        loadTasks()
    }

    private fun loadTasks() {
        getTasksJob?.cancel()
        getTasksJob = viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.getTasks().collect { value ->
                val newTasks = value
                    .filter {
                        _uiState.value.selectedTaskFilter.predicate(it)
                    }.map {
                        it.toUiModel()
                    }
                _uiState.update { it.copy(tasks = newTasks) }
            }
        }
    }

    fun updateTask(isChecked: Boolean, task: TaskUiModel) {
        viewModelScope.launch {
            tasksRepository.updateTask(task.uuid, isChecked)
        }
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isRefreshing = true) }
            tasksRepository.oneTimeSync()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

}


fun Task.toUiModel(): TaskUiModel {
    return TaskUiModel(uuid, title, description, isCompleted)
}
