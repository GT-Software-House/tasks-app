package org.gabrielsantana.tasks.di

import org.gabrielsantana.tasks.TasksDatabase
import org.gabrielsantana.tasks.data.TasksRepository
import org.gabrielsantana.tasks.data.driver.DatabaseDriverFactory
import org.gabrielsantana.tasks.data.source.TasksLocalDataSource
import org.gabrielsantana.tasks.features.create.di.createTaskModule
import org.gabrielsantana.tasks.features.home.di.homeModule
import org.gabrielsantana.tasks.features.settings.appearance.di.prefencresModule
import org.koin.dsl.module

val appModule = module {
    single { TasksDatabase(get<DatabaseDriverFactory>().createDriver())}
    single { TasksLocalDataSource(get()) }
    includes(homeModule, createTaskModule, prefencresModule)
    single { TasksRepository(get()) }
}
