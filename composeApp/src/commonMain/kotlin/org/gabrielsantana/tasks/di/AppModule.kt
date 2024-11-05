package org.gabrielsantana.tasks.di


import androidx.lifecycle.viewmodel.compose.viewModel
import org.gabrielsantana.tasks.AppViewModel
import org.gabrielsantana.tasks.DatabaseDriverFactory
import org.gabrielsantana.tasks.TasksDatabase
import org.gabrielsantana.tasks.TasksLocalDataSource
import org.gabrielsantana.tasks.features.create.CreateTaskViewModel
import org.gabrielsantana.tasks.features.create.di.createTaskModule
import org.gabrielsantana.tasks.features.home.HomeViewModel
import org.gabrielsantana.tasks.features.home.di.homeModule
import org.koin.core.context.loadKoinModules
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { TasksDatabase(get<DatabaseDriverFactory>().createDriver())}
    single { TasksLocalDataSource(get()) }
    viewModelOf(::AppViewModel)
    includes(homeModule, createTaskModule)
}
