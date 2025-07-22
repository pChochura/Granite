package com.pointlessapps.granite.di

import com.pointlessapps.granite.MainViewModel
import com.pointlessapps.granite.editor.di.editorModule
import com.pointlessapps.granite.home.di.homeModule
import com.pointlessapps.granite.login.di.loginModule
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private val applicationModule = module {
    viewModelOf(::MainViewModel)
}

internal val applicationModules = listOf(applicationModule, loginModule, homeModule, editorModule)
