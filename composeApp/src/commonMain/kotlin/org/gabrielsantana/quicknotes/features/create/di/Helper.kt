package org.gabrielsantana.quicknotes.features.create.di

import org.gabrielsantana.quicknotes.features.create.ui.CreateTaskViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object CreateTaskViewModelHelper : KoinComponent {
    fun provideCreateTaskViewModel(): CreateTaskViewModel = get()
}