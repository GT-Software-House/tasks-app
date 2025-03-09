package org.gabrielsantana.quicknotes.data.task

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath


/**
 * Gets the singleton DataStore instance, creating it if necessary.
 */
internal fun createDataStore(producePath: (fileName: String) -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath( "dice.preferences_pb").toPath() }
    )
