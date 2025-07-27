package com.pointlessapps.granite.editor.di

import com.pointlessapps.granite.editor.ui.EditorViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal val editorModule = module {
    viewModel { params ->
        EditorViewModel(
            savedStateHandle = get(),
            application = get(),
            arg = params.get(),
        )
    }
}
