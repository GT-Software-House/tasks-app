package org.gabrielsantana.tasks.features.home.di

import org.gabrielsantana.tasks.features.home.ui.HomeViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object HomeViewModelHelper : KoinComponent {
    fun provideHomeViewModel(): HomeViewModel = get()
}