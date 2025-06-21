package com.pointlessapps.granite.domain.prefs.di

import com.pointlessapps.granite.domain.prefs.PrefsRepository
import com.pointlessapps.granite.domain.prefs.PrefsRepositoryImpl
import com.pointlessapps.granite.domain.prefs.usecase.GetItemsOrderTypeUseCase
import com.pointlessapps.granite.domain.prefs.usecase.GetLastOpenedFileUseCase
import com.pointlessapps.granite.domain.prefs.usecase.SetItemsOrderTypeUseCase
import com.pointlessapps.granite.domain.prefs.usecase.SetLastOpenedFileUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val prefsModule = module {
    factoryOf(::SetLastOpenedFileUseCase)
    factoryOf(::GetLastOpenedFileUseCase)
    factoryOf(::SetItemsOrderTypeUseCase)
    factoryOf(::GetItemsOrderTypeUseCase)

    singleOf(::PrefsRepositoryImpl).bind(PrefsRepository::class)
}
