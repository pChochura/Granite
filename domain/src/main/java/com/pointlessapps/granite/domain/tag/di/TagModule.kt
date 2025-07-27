package com.pointlessapps.granite.domain.tag.di

import com.pointlessapps.granite.domain.tag.usecase.GetDailyNoteTagIdUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val tagModule = module {
    factoryOf(::GetDailyNoteTagIdUseCase)
}
