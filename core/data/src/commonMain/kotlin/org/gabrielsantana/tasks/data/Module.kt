package org.gabrielsantana.tasks.data

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import org.gabrielsantana.tasks.core.data.TasksDatabase
import org.gabrielsantana.tasks.data.driver.DatabaseDriverFactory
import org.gabrielsantana.tasks.data.source.local.TasksLocalDataSource
import org.gabrielsantana.tasks.data.source.remote.TasksRemoteDataSource
import org.koin.core.module.Module
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
val coreDataModule = module {
    includes(specificPlatformModule)
    single { TasksDatabase(get<DatabaseDriverFactory>().createDriver()) }
    factory<TaskSyncer> { TaskSyncer(get(), get(), get()) }
    single { TasksLocalDataSource(get()) }
    single { TasksRemoteDataSource(get()) }
    factory<TasksRepository> { TasksRepositoryImpl(get(), get(), get()) }
    factory<FlowSettings> { DataStoreSettings(get()) }
    factory<SyncerPreferences> { TasksSyncerPreferences(get()) }
}

expect val specificPlatformModule: Module