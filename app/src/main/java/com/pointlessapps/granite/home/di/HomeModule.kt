package com.pointlessapps.granite.home.di

import android.content.Context
import com.pointlessapps.granite.R
import com.pointlessapps.granite.home.ui.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal val homeModule = module {
    viewModel {
        HomeViewModel(
            savedStateHandle = get(),
            getNotesUseCase = get(),
            updateItemUseCase = get(),
            createItemUseCase = get(),
            untitledNotePlaceholder = get<Context>().getString(R.string.untitled),
        )
    }
}
