package org.gabrielsantana.tasks.features.home.di

import org.gabrielsantana.tasks.features.home.ui.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    viewModelOf(::HomeViewModel)
}
