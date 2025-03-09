package org.gabrielsantana.quicknotes.features.create.di

import org.gabrielsantana.quicknotes.features.create.ui.CreateTaskViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val createTaskModule = module {
    viewModelOf(::CreateTaskViewModel)
}