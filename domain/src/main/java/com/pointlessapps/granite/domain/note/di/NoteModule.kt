package com.pointlessapps.granite.domain.note.di

import com.pointlessapps.granite.domain.note.NoteRepository
import com.pointlessapps.granite.domain.note.NoteRepositoryImpl
import com.pointlessapps.granite.domain.note.usecase.GetNotesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val noteModule = module {
    factoryOf(::GetNotesUseCase)

    singleOf(::NoteRepositoryImpl).bind(NoteRepository::class)
}
