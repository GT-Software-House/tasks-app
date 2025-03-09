package org.gabrielsantana.tasks.di

import org.gabrielsantana.quicknote.data.task.data.createDataStore
import org.koin.dsl.module
import org.gabrielsantana.tasks.data.driver.IOSDatabaseDriverFactory
import org.gabrielsantana.quicknote.data.task.data.driver.DatabaseDriverFactory

val iosModule = module {
    single { createDataStore() }
    factory<DatabaseDriverFactory> { IOSDatabaseDriverFactory() }
}