package org.gabrielsantana.tasks.di

import org.gabrielsantana.tasks.data.driver.DatabaseDriverFactory
import org.gabrielsantana.tasks.data.driver.DesktopDatabaseDriverFactory
import org.koin.dsl.module

val desktopModule = module {
    factory<DatabaseDriverFactory> { DesktopDatabaseDriverFactory() }
}