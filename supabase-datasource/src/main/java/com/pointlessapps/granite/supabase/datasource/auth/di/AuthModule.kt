package com.pointlessapps.granite.supabase.datasource.auth.di

import com.pointlessapps.granite.datasource.auth.AuthDatasource
import com.pointlessapps.granite.supabase.datasource.auth.SupabaseAuthDatasource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val authModule = module {
    singleOf(::SupabaseAuthDatasource).bind<AuthDatasource>()
}
