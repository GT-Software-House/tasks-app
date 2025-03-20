@file:OptIn(ExperimentalUuidApi::class)

package org.gabrielsantana.quicknotes.feature.home.ui

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
import org.gabrielsantana.quicknotes.data.task.TasksRepository
import org.gabrielsantana.quicknotes.data.task.model.Task
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class HomeViewModel(
    private val tasksRepository: TasksRepository,
) : ViewModel() {

    private var getTasksJob: Job? = null

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTasks()
        //TODO: collect this in another place?
//        viewModelScope.launch {
//            scheduler.tasksWaitingSync().collect { syncStatus ->
//                _uiState.update {
//                    it.copy(syncStatus = syncStatus)
//                }
//            }
//        }
    }

    fun selectTask(task: TaskUiModel) = _uiState.update { state ->
        if (state.selectedTasksUuid.contains(task.uuid)) {
            state.copy(selectedTasksUuid = state.selectedTasksUuid.toMutableSet().apply { remove(task.uuid) })
        } else {
            state.copy(selectedTasksUuid = state.selectedTasksUuid.toMutableSet().apply { add(task.uuid) })
        }
    }

    fun clearSelectedTasks() = _uiState.update { state ->
        state.copy(selectedTasksUuid = emptySet())
    }

    fun deleteSelectedTasks(): Unit = with(_uiState.value) {
        selectedTasksUuid.forEach { taskUuid ->
            tasksRepository.deleteTask(taskUuid)
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
            doWithRetry(::refresh) {
                tasksRepository.oneTimeSync()
            }
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun doRetryAction() {
        _uiState.value.retryAction?.invoke()
        _uiState.update { it.copy(retryAction = null) }
    }

    private inline fun doWithRetry(noinline retry: () -> Unit, action: () -> Unit ) {
        try {
            action()
        } catch (_: Throwable) {
            _uiState.update {
                it.copy(retryAction = retry)
            }
        }
    }

}


