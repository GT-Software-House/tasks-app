package org.gabrielsantana.quicknotes.feature.preferences.appearance.di

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import org.gabrielsantana.quicknotes.feature.preferences.appearance.data.PreferencesRepository
import org.gabrielsantana.quicknotes.feature.preferences.appearance.ui.AppearanceViewModel
import org.koin.core.module.dsl.viewModelOf

import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
val preferencesModule = module {
    single { PreferencesRepository(get()) }
    viewModelOf(::AppearanceViewModel)
}