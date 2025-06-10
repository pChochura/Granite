package com.pointlessapps.granite.supabase.datasource.di

import com.pointlessapps.granite.supabase.datasource.auth.di.authModule
import com.pointlessapps.granite.supabase.datasource.note.di.noteModule
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.dsl.module

private val supabaseModule = module {
    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = getProperty("SUPABASE_URL"),
            supabaseKey = getProperty("SUPABASE_KEY"),
        ) {
            install(Postgrest)
            install(Auth)
        }
    }
}

val supabaseModules = listOf(supabaseModule, authModule, noteModule)
