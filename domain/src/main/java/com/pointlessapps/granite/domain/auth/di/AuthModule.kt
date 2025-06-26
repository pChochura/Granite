package com.pointlessapps.granite.domain.auth.di

import com.pointlessapps.granite.domain.auth.usecase.InitializeSupabaseUseCase
import com.pointlessapps.granite.domain.auth.usecase.IsSignedInUseCase
import com.pointlessapps.granite.domain.auth.usecase.SignInAnonymouslyUseCase
import com.pointlessapps.granite.domain.auth.usecase.SignInUseCase
import com.pointlessapps.granite.domain.auth.usecase.SignInWithGoogleUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val authModule = module {
    factoryOf(::InitializeSupabaseUseCase)
    factoryOf(::IsSignedInUseCase)
    factoryOf(::SignInUseCase)
    factoryOf(::SignInAnonymouslyUseCase)
    factory {
        SignInWithGoogleUseCase(
            authDatasource = get(),
            googleWebClientId = getProperty("GOOGLE_WEB_CLIENT_ID"),
        )
    }
}
