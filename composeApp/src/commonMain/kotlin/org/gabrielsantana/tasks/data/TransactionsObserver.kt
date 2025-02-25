package org.gabrielsantana.tasks.data

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class TransactionsObserver(
    private val tasksRepository: TasksRepository
) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)

    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)

    }
}