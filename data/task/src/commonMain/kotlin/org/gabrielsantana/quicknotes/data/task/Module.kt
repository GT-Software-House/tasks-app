package org.gabrielsantana.quicknotes.data.task

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import org.gabrielsantana.quicknotes.data.task.driver.DatabaseDriverFactory
import org.gabrielsantana.quicknotes.data.task.source.local.TasksLocalDataSource
import org.gabrielsantana.quicknotes.data.task.source.local.adapter.UuidColumnAdapter
import org.gabrielsantana.quicknotes.data.task.source.remote.TasksRemoteDataSource
import org.koin.core.module.Module
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
val coreDataModule = module {
    includes(specificPlatformModule)
    single { TaskDatabase(
        driver = get<DatabaseDriverFactory>().createDriver(),
        TaskEntityAdapter = TaskEntity.Adapter(uuidAdapter = UuidColumnAdapter())
        )
    }
    factory<TaskSyncer> { TaskSyncer(get(), get(), get()) }
    single { TasksLocalDataSource(get()) }
    single { TasksRemoteDataSource(get()) }
    factory<TasksRepository> { TasksRepositoryImpl(get(), get(), get()) }
    factory<FlowSettings> { DataStoreSettings(get()) }
    factory<SyncerPreferences> { TasksSyncerPreferences(get()) }
}

expect val specificPlatformModule: Module