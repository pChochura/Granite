package com.pointlessapps.granite.domain.note.di

import com.pointlessapps.granite.domain.note.NoteRepository
import com.pointlessapps.granite.domain.note.NoteRepositoryImpl
import com.pointlessapps.granite.domain.note.usecase.CreateItemUseCase
import com.pointlessapps.granite.domain.note.usecase.DeleteItemsUseCase
import com.pointlessapps.granite.domain.note.usecase.DuplicateItemsUseCase
import com.pointlessapps.granite.domain.note.usecase.GetNoteUseCase
import com.pointlessapps.granite.domain.note.usecase.GetNotesUseCase
import com.pointlessapps.granite.domain.note.usecase.MarkItemsAsDeletedUseCase
import com.pointlessapps.granite.domain.note.usecase.MoveItemUseCase
import com.pointlessapps.granite.domain.note.usecase.UpdateItemUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val noteModule = module {
    factoryOf(::GetNotesUseCase)
    factoryOf(::GetNoteUseCase)
    factoryOf(::UpdateItemUseCase)
    factoryOf(::CreateItemUseCase)
    factoryOf(::MarkItemsAsDeletedUseCase)
    factoryOf(::DuplicateItemsUseCase)
    factoryOf(::DeleteItemsUseCase)
    factoryOf(::MoveItemUseCase)

    singleOf(::NoteRepositoryImpl).bind(NoteRepository::class)
}
