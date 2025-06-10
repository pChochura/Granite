package com.pointlessapps.granite

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.granite.domain.auth.usecase.InitializeSupabaseUseCase
import com.pointlessapps.granite.domain.auth.usecase.IsSignedInUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

internal class MainViewModel(
    initializeSupabaseUseCase: InitializeSupabaseUseCase,
    private val isSignedInUseCase: IsSignedInUseCase,
) : ViewModel() {

    var isInitialized by mutableStateOf(false)

    init {
        initializeSupabaseUseCase()
            .onStart { isInitialized = false }
            .onEach { isInitialized = true }
            .catch { isInitialized = true }
            .launchIn(viewModelScope)
    }

    fun isSignedIn() = isSignedInUseCase()
}
