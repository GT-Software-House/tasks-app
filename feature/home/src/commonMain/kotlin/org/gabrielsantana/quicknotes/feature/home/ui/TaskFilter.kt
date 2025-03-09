package org.gabrielsantana.quicknotes.feature.home.ui

import org.gabrielsantana.quicknotes.data.task.model.Task


enum class TaskFilter(val label: String, val predicate: Predicate<Task>) {
    //Maybe it's unnecessary
    ALL("All", { true }),
    DONE("Done", { it.isCompleted }),
    DOING("Doing", { it.isCompleted.not() })
}

fun interface Predicate<T> {
    operator fun invoke(value: T): Boolean
}
