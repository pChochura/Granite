package com.pointlessapps.granite.di

import com.pointlessapps.granite.BuildConfig
import com.pointlessapps.granite.home.di.homeModule
import com.pointlessapps.granite.login.di.loginModule
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.dsl.module

private val applicationModule = module {
    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY,
        ) {
            install(Postgrest)
            install(Auth) {
                scheme = "https"
                host = "supabase.com"
            }
        }
    }
}

internal val applicationModules = listOf(applicationModule, loginModule, homeModule)
