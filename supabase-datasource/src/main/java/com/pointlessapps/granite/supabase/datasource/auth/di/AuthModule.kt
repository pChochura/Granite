package com.pointlessapps.granite.supabase.datasource.auth.di

import com.pointlessapps.granite.supabase.datasource.auth.SupabaseAuthDatasource
import com.pointlessapps.granite.supabase.datasource.auth.SupabaseAuthDatasourceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val authModule = module {
    singleOf(::SupabaseAuthDatasourceImpl).bind<SupabaseAuthDatasource>()
}
