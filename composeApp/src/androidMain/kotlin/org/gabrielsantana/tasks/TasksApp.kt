package org.gabrielsantana.tasks

import android.app.Application
import android.content.Context
import org.gabrielsantana.tasks.data.createDataStore
import org.gabrielsantana.tasks.data.dataStoreFileName
import org.gabrielsantana.tasks.data.driver.AndroidDatabaseDriverFactory
import org.gabrielsantana.tasks.data.driver.DatabaseDriverFactory
import org.gabrielsantana.tasks.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class TasksApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val androidModule = module {
            factory<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(this@TasksApp) }
            single {
                val context: Context = get()
                createDataStore(producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath })
            }
        }

        startKoin {
            androidContext(this@TasksApp)
            modules(androidModule + appModule)
        }
    }

}