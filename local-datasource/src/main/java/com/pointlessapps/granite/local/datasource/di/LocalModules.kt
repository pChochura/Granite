package com.pointlessapps.granite.local.datasource.di

import androidx.room.Room
import com.pointlessapps.granite.local.datasource.LocalDatabase
import com.pointlessapps.granite.local.datasource.note.di.noteModule
import org.koin.dsl.module

private val localModule = module {
    single<LocalDatabase> {
        Room.databaseBuilder(
            context = get(),
            klass = LocalDatabase::class.java,
            name = "local_database",
        ).fallbackToDestructiveMigration(true)
            .build()
    }
}

val localModules = listOf(localModule, noteModule)
