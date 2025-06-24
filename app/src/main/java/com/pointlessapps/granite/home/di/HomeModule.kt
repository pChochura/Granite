package com.pointlessapps.granite.home.di

import android.content.Context
import com.pointlessapps.granite.R
import com.pointlessapps.granite.home.ui.HomeViewModel
import com.pointlessapps.granite.home.ui.delegates.ItemCreationDelegate
import com.pointlessapps.granite.home.ui.delegates.ItemDeletionDelegate
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val homeModule = module {
    single { params ->
        ItemCreationDelegate(
            savedStateHandle = params.get(),
            untitledNotePlaceholder = get<Context>().getString(R.string.untitled),
        )
    }
    singleOf(::ItemDeletionDelegate)

    viewModelOf(::HomeViewModel)
}
