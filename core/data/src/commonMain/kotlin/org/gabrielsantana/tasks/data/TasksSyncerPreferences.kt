package org.gabrielsantana.tasks.data

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalSettingsApi::class)
class TasksSyncerPreferences(
    private val flowSettings: FlowSettings
) : SyncerPreferences {
    override suspend fun getLastSync(): String? {
        return flowSettings.getStringOrNull("last_sync")
    }

    override fun getLastSyncFlow(): Flow<String?> {
        return flowSettings.getStringOrNullFlow("last_sync")
    }

    override suspend fun updateLastSync(lastSync: String) {
        flowSettings.putString("last_sync", lastSync)
    }
}