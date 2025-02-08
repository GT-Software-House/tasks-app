package org.gabrielsantana.tasks

import android.app.Application
import org.gabrielsantana.tasks.auth.login.startFirebaseAuth
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
        }

        startKoin {
            androidContext(this@TasksApp)
            modules(androidModule + appModule)
        }
        startFirebaseAuth(getString(R.string.default_web_client_id))
    }
}