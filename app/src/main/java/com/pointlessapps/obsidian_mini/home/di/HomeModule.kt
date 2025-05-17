package com.pointlessapps.obsidian_mini.home.di

import com.pointlessapps.obsidian_mini.home.ui.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val homeModule = module {
    viewModelOf(::HomeViewModel)
}
