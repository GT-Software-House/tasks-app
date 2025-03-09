package org.gabrielsantana.quicknotes.di

import org.gabrielsantana.quicknotes.data.driver.DatabaseDriverFactory
import org.gabrielsantana.quicknotes.data.driver.DesktopDatabaseDriverFactory
import org.koin.dsl.module

val desktopModule = module {
    factory<DatabaseDriverFactory> { DesktopDatabaseDriverFactory() }
}