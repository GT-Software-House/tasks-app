package org.gabrielsantana.quicknotes.initializer

import android.content.Context
import androidx.startup.Initializer
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.gabrielsantana.quicknotes.data.task.TasksRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppStartupSync : Initializer<Unit>, KoinComponent {

    companion object {
        private const val TAG = "AppStartupSync"
    }

    private val repository: TasksRepository by inject()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun create(context: Context) {
        scope.launch {
            try {
                repository.oneTimeSync()
            } catch (exception: Exception) {
                Logger.e(TAG, exception) {
                    "Error on startup sync"
                }
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return listOf(KoinInitializer::class.java)
    }
}