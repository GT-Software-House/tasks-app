package org.gabrielsantana.quicknotes.di

import org.koin.core.context.startKoin

fun initKoin(){
    startKoin {
        modules(appModule + iosModule)
    }
}