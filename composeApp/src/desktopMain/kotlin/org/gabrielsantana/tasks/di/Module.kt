package org.gabrielsantana.tasks.di

import org.gabrielsantana.tasks.DatabaseDriverFactory
import org.gabrielsantana.tasks.DesktopDatabaseDriverFactory
import org.koin.dsl.module

val desktopModule = module {
    factory<DatabaseDriverFactory> { DesktopDatabaseDriverFactory() }
}