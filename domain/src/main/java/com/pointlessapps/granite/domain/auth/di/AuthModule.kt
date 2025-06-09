package com.pointlessapps.granite.domain.auth.di

import com.pointlessapps.granite.domain.auth.AuthRepository
import com.pointlessapps.granite.domain.auth.AuthRepositoryImpl
import com.pointlessapps.granite.domain.auth.usecase.IsSignedInUseCase
import com.pointlessapps.granite.domain.auth.usecase.SignInAnonymouslyUseCase
import com.pointlessapps.granite.domain.auth.usecase.SignInUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val authModule = module {
    factoryOf(::IsSignedInUseCase)
    factoryOf(::SignInUseCase)
    factoryOf(::SignInAnonymouslyUseCase)

    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
}
