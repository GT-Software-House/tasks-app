package org.gabrielsantana.quicknotes.feature.home.di

import org.gabrielsantana.quicknotes.feature.home.ui.HomeViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object HomeViewModelHelper : KoinComponent {
    fun provideHomeViewModel(): HomeViewModel = get()
}