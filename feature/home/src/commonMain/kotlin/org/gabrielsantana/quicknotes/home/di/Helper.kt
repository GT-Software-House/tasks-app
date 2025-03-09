package org.gabrielsantana.quicknotes.home.di

import org.gabrielsantana.quicknotes.home.ui.HomeViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object HomeViewModelHelper : KoinComponent {
    fun provideHomeViewModel(): HomeViewModel = get()
}