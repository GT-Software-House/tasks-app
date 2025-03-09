package org.gabrielsantana.quicknotes.initializer

import android.content.Context
import androidx.startup.Initializer
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.KoinAppDeclaration

class KoinInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        if (context !is KoinDeclaration) {
            error("KoinInitializer can't start Koin configuration. Please implement KoinDeclaration interface in your Application class")
        }
        startKoin(context.declaration())
    }


    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return emptyList()
    }
}

interface KoinDeclaration {
    fun declaration(): KoinAppDeclaration
}