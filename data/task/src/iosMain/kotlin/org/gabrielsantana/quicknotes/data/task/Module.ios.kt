package org.gabrielsantana.quicknotes.data.task

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import org.gabrielsantana.quicknotes.data.task.driver.DatabaseDriverFactory
import org.gabrielsantana.quicknotes.data.task.driver.IOSDatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual val specificPlatformModule: Module = module {
    factory<DataStore<Preferences>> { createDataStore() }
    factory<DatabaseDriverFactory> { IOSDatabaseDriverFactory() }
}

@OptIn(ExperimentalForeignApi::class)
private fun createDataStore(): DataStore<Preferences> = createDataStore(
    producePath = { fileName ->
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        requireNotNull(documentDirectory).path + "/$fileName"
    }
)