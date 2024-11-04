package org.gabrielsantana.tasks.di


import org.gabrielsantana.tasks.AppViewModel
import org.gabrielsantana.tasks.DatabaseDriverFactory
import org.gabrielsantana.tasks.TasksDatabase
import org.gabrielsantana.tasks.TasksLocalDataSource
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { TasksDatabase(get<DatabaseDriverFactory>().createDriver())}
    single { TasksLocalDataSource(get()) }
    viewModelOf(::AppViewModel)
}
