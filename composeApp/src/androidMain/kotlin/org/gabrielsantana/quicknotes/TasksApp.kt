package org.gabrielsantana.quicknotes

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import org.gabrielsantana.quicknotes.di.appModule
import org.gabrielsantana.quicknotes.initializer.KoinDeclaration
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.dsl.KoinAppDeclaration

class TasksApp : Application(), Configuration.Provider, KoinDeclaration {

    companion object {
        @Volatile
        private var appContext: Context? = null

        fun getAppContext(): Context {
            return appContext!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
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

    override fun declaration(): KoinAppDeclaration = {
        androidContext(this@TasksApp)
        modules(appModule)
        workManagerFactory()
    }

}
