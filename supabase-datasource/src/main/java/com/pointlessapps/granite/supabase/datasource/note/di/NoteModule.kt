package com.pointlessapps.granite.supabase.datasource.note.di

import com.pointlessapps.granite.supabase.datasource.note.SupabaseNoteDatasource
import com.pointlessapps.granite.supabase.datasource.note.SupabaseNoteDatasourceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val noteModule = module {
    singleOf(::SupabaseNoteDatasourceImpl).bind<SupabaseNoteDatasource>()
}
