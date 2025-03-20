package org.gabrielsantana.quicknotes.data.task

import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface Syncer {
    suspend fun oneTimeSync()
    suspend fun reactiveSync()

    /**
     * @return if the data was synced successfully
     */
    suspend fun syncToRemote(taskUuid: Uuid): Boolean
}

internal interface SyncerPreferences {
    suspend fun getLastSync(): String?
    fun getLastSyncFlow(): Flow<String?>
    suspend fun updateLastSync(lastSync: String)
}
