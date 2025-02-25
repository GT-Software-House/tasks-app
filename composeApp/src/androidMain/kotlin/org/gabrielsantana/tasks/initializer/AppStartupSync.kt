package org.gabrielsantana.tasks.initializer

import android.content.Context
import androidx.startup.Initializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.gabrielsantana.tasks.data.TasksRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppStartupSync : Initializer<Unit>, KoinComponent {

    private val repository: TasksRepository by inject()

    override fun create(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.sync()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return listOf(KoinInitializer::class.java)
    }
}