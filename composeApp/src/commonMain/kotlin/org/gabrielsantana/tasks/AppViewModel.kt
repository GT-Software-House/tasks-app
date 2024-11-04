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
            delay(2000L)
            name.update { "Gabriel" }
        }
    }

}