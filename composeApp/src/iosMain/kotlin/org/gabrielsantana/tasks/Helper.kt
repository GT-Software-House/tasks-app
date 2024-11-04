package org.gabrielsantana.tasks

import org.gabrielsantana.tasks.di.appModule
import org.koin.core.context.startKoin

fun initKoin(){
    startKoin {
        modules(appModule + iosModule)
    }
}