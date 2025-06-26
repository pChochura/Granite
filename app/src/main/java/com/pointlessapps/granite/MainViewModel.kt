package com.pointlessapps.granite

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pointlessapps.granite.domain.auth.usecase.InitializeSupabaseUseCase
import com.pointlessapps.granite.domain.auth.usecase.IsSignedInUseCase
import com.pointlessapps.granite.utils.launch

internal class MainViewModel(
    initializeSupabaseUseCase: InitializeSupabaseUseCase,
    private val isSignedInUseCase: IsSignedInUseCase,
) : ViewModel() {

    var isInitialized by mutableStateOf(false)

    init {
        launch(onException = { isInitialized = false }) {
            initializeSupabaseUseCase()
            isInitialized = true
        }
    }

    fun isSignedIn() = isSignedInUseCase()
}
