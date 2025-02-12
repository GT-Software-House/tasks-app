package org.gabrielsantana.tasks.di


import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import org.gabrielsantana.tasks.TasksDatabase
import org.gabrielsantana.tasks.data.TasksRepository
import org.gabrielsantana.tasks.data.driver.DatabaseDriverFactory
import org.gabrielsantana.tasks.data.source.TasksLocalDataSource
import org.gabrielsantana.tasks.features.create.di.createTaskModule
import org.gabrielsantana.tasks.features.home.di.homeModule
import org.gabrielsantana.tasks.features.settings.appearance.di.prefencresModule
import org.koin.dsl.module

val appModule = module {
    single { TasksDatabase(get<DatabaseDriverFactory>().createDriver()) }
    single { TasksLocalDataSource(get()) }
    includes(homeModule, createTaskModule, prefencresModule)
    single { TasksRepository(get()) }
    single {
        createSupabaseClient(SUPABASE_URL,  SUPABASE_KEY) {
            install(Auth)
            install(ComposeAuth) {
                googleNativeLogin(SERVER_CLIENT_ID)
            }
        }
    }
}

private const val SUPABASE_URL = "https://utkombbmvumyqqglhywk.supabase.co"
private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InV0a29tYmJtdnVteXFxZ2xoeXdrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzkxODU4NDgsImV4cCI6MjA1NDc2MTg0OH0.dq89ZUA9u6e_NJTe8MmD-7x9OP2fwmU3CeBQ2Psncpk"
private const val SERVER_CLIENT_ID = "111134972257-sf4n11l8nlt1l53b8ktau5m13ijiiq2s.apps.googleusercontent.com"


