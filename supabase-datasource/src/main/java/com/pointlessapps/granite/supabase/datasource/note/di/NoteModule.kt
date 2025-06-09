package com.pointlessapps.granite.supabase.datasource.note.di

import com.pointlessapps.granite.datasource.note.NoteDatasource
import com.pointlessapps.granite.supabase.datasource.note.SupabaseNoteDatasource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val noteModule = module {
    singleOf(::SupabaseNoteDatasource).bind<NoteDatasource>()
}
