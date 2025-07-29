package com.pointlessapps.granite.domain.note.di

import com.pointlessapps.granite.domain.note.usecase.CreateDailyNoteUseCase
import com.pointlessapps.granite.domain.note.usecase.CreateItemUseCase
import com.pointlessapps.granite.domain.note.usecase.DeleteItemsUseCase
import com.pointlessapps.granite.domain.note.usecase.DuplicateItemsUseCase
import com.pointlessapps.granite.domain.note.usecase.GetDailyNoteUseCase
import com.pointlessapps.granite.domain.note.usecase.GetNoteUseCase
import com.pointlessapps.granite.domain.note.usecase.GetNotesUseCase
import com.pointlessapps.granite.domain.note.usecase.GetTodayDailyNoteUseCase
import com.pointlessapps.granite.domain.note.usecase.MarkItemsAsDeletedUseCase
import com.pointlessapps.granite.domain.note.usecase.MoveItemUseCase
import com.pointlessapps.granite.domain.note.usecase.UpdateItemUseCase
import org.koin.core.module.dsl.factoryOf
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
    factoryOf(::CreateDailyNoteUseCase)
    factoryOf(::GetDailyNoteUseCase)
    factoryOf(::GetTodayDailyNoteUseCase)
}
