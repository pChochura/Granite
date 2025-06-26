package com.pointlessapps.granite.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal fun ViewModel.launch(
    onException: (Throwable) -> Unit = { it.printStackTrace() },
    block: suspend CoroutineScope.() -> Unit,
) = viewModelScope.launch(
    context = CoroutineExceptionHandler { _, throwable -> onException(throwable) },
    block = block,
)
