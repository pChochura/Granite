package com.pointlessapps.granite.login.di

import com.pointlessapps.granite.login.ui.LoginViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val loginModule = module {
    viewModelOf(::LoginViewModel)
}
