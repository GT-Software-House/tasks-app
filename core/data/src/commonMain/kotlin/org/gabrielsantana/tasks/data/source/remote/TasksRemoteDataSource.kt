@file:OptIn(SupabaseExperimental::class)

package org.gabrielsantana.tasks.data.source.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.Flow
import org.gabrielsantana.tasks.data.source.remote.model.TaskNetworkModel
import org.gabrielsantana.tasks.data.source.remote.model.TaskTransaction

internal class TasksRemoteDataSource(
    private val supabaseClient: SupabaseClient
) {

    companion object {
        private const val TABLE_NAME_TASKS = "tasks"
        private const val TABLE_NAME_TASKS_TRANSACTIONS_HISTORY = "tasks_transactions_history"
    }

    suspend fun insert(
        task: TaskNetworkModel
    ) {
        supabaseClient.from(TABLE_NAME_TASKS).insert(task)
    }

    suspend fun upsert(
        task: TaskNetworkModel,
    ): Boolean {
        val count = supabaseClient.from(TABLE_NAME_TASKS).upsert(task) {
            count(Count.EXACT)
        }.countOrNull() ?: 0L
        return count > 0L
    }

    suspend fun getByIds(uuids: List<String>): List<TaskNetworkModel> {
        return supabaseClient.from(TABLE_NAME_TASKS).select() {
            filter {
                TaskNetworkModel::uuid isIn uuids
            }
        }.decodeList()
    }

    suspend fun getTransactionHistoryAfter(timestamp: String?): List<TaskTransaction> {
        return supabaseClient.from(TABLE_NAME_TASKS_TRANSACTIONS_HISTORY).select {
            filter {
                timestamp?.let { gt("created_at", it) }
            }
            order("created_at", order = Order.ASCENDING)
        }.decodeList<TaskTransaction>()
    }

    suspend fun deleteById(uuid: String): Boolean {
        val result = supabaseClient.from(TABLE_NAME_TASKS).delete {
            count(Count.EXACT)
            filter {
                TaskNetworkModel::uuid eq uuid
            }
        }.countOrNull() ?: 0
        return result > 0L
    }

    fun getNewTransactions(lastSync: String?): Flow<List<TaskTransaction>> {
        return supabaseClient.from(TABLE_NAME_TASKS_TRANSACTIONS_HISTORY).selectAsFlow(
            TaskTransaction::id,
            filter = lastSync?.let { FilterOperation("created_at", FilterOperator.GT, lastSync) }
        )
    }
}