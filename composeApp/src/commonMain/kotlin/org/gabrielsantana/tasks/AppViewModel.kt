package org.gabrielsantana.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    private val tasksLocalDataSource: TasksLocalDataSource
) : ViewModel() {

    val name = MutableStateFlow("")

    init {
        viewModelScope.launch {
            for(x in 1..5) {
                name.update { "Hello${".".repeat(x)}" }
                delay(500L)
            }
            name.update { "Hello, Gabriel!" }
        }
    }

}