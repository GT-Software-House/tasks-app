package org.gabrielsantana.tasks

import org.koin.dsl.module

val iosModule = module {
    factory<DatabaseDriverFactory> { IOSDatabaseDriverFactory() }
}