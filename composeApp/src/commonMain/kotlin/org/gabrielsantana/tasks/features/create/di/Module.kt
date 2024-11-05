package org.gabrielsantana.tasks.features.create.di

import org.gabrielsantana.tasks.features.create.CreateTaskViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val createTaskModule = module {
    viewModelOf(::CreateTaskViewModel)
}