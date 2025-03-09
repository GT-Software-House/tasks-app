package org.gabrielsantana.quicknotes.data.task

import kotlinx.coroutines.flow.Flow

interface Syncer {
    suspend fun oneTimeSync()
    suspend fun reactiveSync()

    /**
     * @return if the data was synced successfully
     */
    suspend fun syncToRemote(taskUuid: String): Boolean
}

internal interface SyncerPreferences {
    suspend fun getLastSync(): String?
    fun getLastSyncFlow(): Flow<String?>
    suspend fun updateLastSync(lastSync: String)
}
