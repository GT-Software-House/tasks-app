package org.gabrielsantana.tasks.data

import android.content.Context
import org.gabrielsantana.data.driver.AndroidDatabaseDriverFactory
import org.gabrielsantana.data.worker.SyncTaskRemotelyWorker
import org.gabrielsantana.data.worker.TaskDeleteSyncerWorker
import org.gabrielsantana.data.worker.TaskUpdateSyncerWorker
import org.gabrielsantana.data.worker.scheduler.AndroidTaskSyncScheduler
import org.gabrielsantana.tasks.data.driver.DatabaseDriverFactory
import org.gabrielsantana.tasks.data.scheduler.TaskSyncScheduler
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
    single {
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