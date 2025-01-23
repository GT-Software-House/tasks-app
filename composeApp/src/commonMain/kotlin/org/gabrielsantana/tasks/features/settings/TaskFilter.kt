package org.gabrielsantana.tasks.features.home.ui

import org.gabrielsantana.tasks.Task

enum class TaskFilter(val label: String, val predicate: Predicate<Task>) {
    //Maybe it's unnecessary
    ALL("All", { true }),
    DONE("Done", { it.isChecked > 0L }),
    DOING("Doing", { it.isChecked == 0L })
}

fun interface Predicate<T> {
    operator fun invoke(value: T): Boolean
}
