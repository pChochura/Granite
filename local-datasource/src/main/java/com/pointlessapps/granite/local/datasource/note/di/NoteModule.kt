package com.pointlessapps.granite.local.datasource.note.di

import com.pointlessapps.granite.local.datasource.LocalDatabase
import com.pointlessapps.granite.local.datasource.note.LocalNoteDatasource
import com.pointlessapps.granite.local.datasource.note.LocalNoteDatasourceImpl
import org.koin.dsl.module

internal val noteModule = module {
    single<LocalNoteDatasource> {
        LocalNoteDatasourceImpl(
            noteDao = get<LocalDatabase>().noteDao(),
        )
    }
}
