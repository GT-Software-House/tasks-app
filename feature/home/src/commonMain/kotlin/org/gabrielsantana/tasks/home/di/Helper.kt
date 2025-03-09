package org.gabrielsantana.tasks.home.di

import org.gabrielsantana.tasks.home.ui.HomeViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object HomeViewModelHelper : KoinComponent {
    fun provideHomeViewModel(): HomeViewModel = get()
}