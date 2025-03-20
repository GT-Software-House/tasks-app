package org.gabrielsantana.quicknotes.data.task

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.gabrielsantana.quicknotes.data.task.driver.AndroidDatabaseDriverFactory
import org.gabrielsantana.quicknotes.data.task.worker.SyncTaskRemotelyWorker
import org.gabrielsantana.quicknotes.data.task.worker.TaskDeleteSyncerWorker
import org.gabrielsantana.quicknotes.data.task.worker.TaskUpdateSyncerWorker
import org.gabrielsantana.quicknotes.data.task.worker.scheduler.AndroidTaskSyncScheduler
import org.gabrielsantana.quicknotes.data.task.driver.DatabaseDriverFactory
import org.gabrielsantana.quicknotes.data.task.scheduler.TaskSyncScheduler
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.Module
import org.koin.dsl.module

actual val specificPlatformModule: Module = module {
    factory<TaskSyncScheduler> {
        AndroidTaskSyncScheduler(get())
    }
    factory<DatabaseDriverFactory> {
        AndroidDatabaseDriverFactory(get())
    }
    single<DataStore<Preferences>> {
        val context: Context = get()
        createDataStore(producePath = { fileName -> context.filesDir.resolve(fileName).absolutePath })
    }
    worker {
        SyncTaskRemotelyWorker(get(), get(), get(), get(), get())
    }
    worker {
        TaskUpdateSyncerWorker(get(), get(), get(), get())
    }
    worker {
        TaskDeleteSyncerWorker(get(), get(), get(), get())
    }
}