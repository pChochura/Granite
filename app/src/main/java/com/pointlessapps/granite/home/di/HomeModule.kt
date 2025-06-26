package com.pointlessapps.granite.home.di

import com.pointlessapps.granite.home.ui.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val homeModule = module {
    viewModelOf(::HomeViewModel)
}
