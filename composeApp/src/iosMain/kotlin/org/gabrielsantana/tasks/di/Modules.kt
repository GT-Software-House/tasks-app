package org.gabrielsantana.tasks.di

import org.koin.dsl.module
import org.gabrielsantana.tasks.data.driver.IOSDatabaseDriverFactory
import org.gabrielsantana.tasks.data.driver.DatabaseDriverFactory

val iosModule = module {
    factory<DatabaseDriverFactory> { IOSDatabaseDriverFactory() }
}