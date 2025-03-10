package org.gabrielsantana.quicknotes.di

import dev.jordond.connectivity.Connectivity
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import org.gabrielsantana.quicknotes.data.task.coreDataModule
import org.gabrielsantana.quicknotes.features.create.di.createTaskModule
import org.gabrielsantana.quicknotes.feature.home.di.homeModule
import org.gabrielsantana.quicknotes.feature.preferences.appearance.di.preferencesModule
import org.koin.dsl.module

val appModule = module {
    //TODO: many 'singles', improve this
    includes(coreDataModule, homeModule, createTaskModule, preferencesModule)
    single {
        createSupabaseClient(SUPABASE_URL,  SUPABASE_KEY) {
            install(Auth) {
                //the navigation is controlled by session state and it's cleaned and reloaded on app callbacks, so we need to disable that 'optimization'
                enableLifecycleCallbacks = false
            }
            install(ComposeAuth) {
                googleNativeLogin(SERVER_CLIENT_ID)
            }
            install(Realtime)
            install(Postgrest) {
                defaultSchema = "public"
            }
        }
    }

    single {
        Connectivity {
            autoStart = true
        }
    }
}

private const val SUPABASE_URL = "https://utkombbmvumyqqglhywk.supabase.co"
private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InV0a29tYmJtdnVteXFxZ2xoeXdrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzkxODU4NDgsImV4cCI6MjA1NDc2MTg0OH0.dq89ZUA9u6e_NJTe8MmD-7x9OP2fwmU3CeBQ2Psncpk"
private const val SERVER_CLIENT_ID = "111134972257-sf4n11l8nlt1l53b8ktau5m13ijiiq2s.apps.googleusercontent.com"


