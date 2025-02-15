package org.gabrielsantana.tasks

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import org.gabrielsantana.tasks.data.createDataStore
import org.gabrielsantana.tasks.data.driver.AndroidDatabaseDriverFactory
import org.gabrielsantana.tasks.data.driver.DatabaseDriverFactory
import org.gabrielsantana.tasks.data.scheduler.TaskSyncScheduler
import org.gabrielsantana.tasks.data.worker.SyncTaskRemotelyWorker
import org.gabrielsantana.tasks.data.worker.scheduler.AndroidTaskSyncScheduler
import org.gabrielsantana.tasks.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.dsl.module

class TasksApp : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        val androidModule = module {
            factory<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(this@TasksApp) }
            single {
                val context: Context = get()
                createDataStore(producePath = { fileName -> context.filesDir.resolve(fileName).absolutePath })
            }
            worker {
                SyncTaskRemotelyWorker(get(), get(), get(), get(), get())
            }
            factory<TaskSyncScheduler> {
                AndroidTaskSyncScheduler(get())
            }
        }

        startKoin {
            androidContext(this@TasksApp)
            modules(androidModule + appModule)
            workManagerFactory()
        }
    }

    override val workManagerConfiguration = if (BuildConfig.DEBUG) {
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
    } else {
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.ERROR)
            .build()
    }

}
