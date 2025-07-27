package com.pointlessapps.granite.local.datasource.note.di

import com.pointlessapps.granite.local.datasource.LocalDatabase
import com.pointlessapps.granite.local.datasource.note.LocalNoteDatasource
import com.pointlessapps.granite.local.datasource.note.LocalNoteDatasourceImpl
import com.pointlessapps.granite.local.datasource.note.LocalTagDatasource
import com.pointlessapps.granite.local.datasource.note.LocalTagDatasourceImpl
import org.koin.dsl.module

internal val noteModule = module {
    single<LocalNoteDatasource> {
        LocalNoteDatasourceImpl(
            noteDao = get<LocalDatabase>().noteDao(),
        )
    }
    single<LocalTagDatasource> {
        LocalTagDatasourceImpl(
            tagDao = get<LocalDatabase>().tagDao(),
        )
    }
}
