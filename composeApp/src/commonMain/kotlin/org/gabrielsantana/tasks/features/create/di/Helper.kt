package org.gabrielsantana.tasks.features.create.di

import org.gabrielsantana.tasks.features.create.ui.CreateTaskViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object CreateTaskViewModelHelper : KoinComponent {
    fun provideCreateTaskViewModel(): CreateTaskViewModel = get()
}