package org.gabrielsantana.quicknotes.home.di

import org.gabrielsantana.quicknotes.home.ui.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    viewModelOf(::HomeViewModel)
}
