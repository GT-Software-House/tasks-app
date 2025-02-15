package org.gabrielsantana.tasks.features.settings.appearance.di

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.datastore.DataStoreSettings
import org.gabrielsantana.tasks.features.settings.appearance.data.PreferencesRepository
import org.gabrielsantana.tasks.features.settings.appearance.ui.AppearanceViewModel
import org.koin.core.module.dsl.viewModelOf

import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
val preferencesModule = module {
    single { PreferencesRepository(DataStoreSettings(get())) }
    viewModelOf(::AppearanceViewModel)
}