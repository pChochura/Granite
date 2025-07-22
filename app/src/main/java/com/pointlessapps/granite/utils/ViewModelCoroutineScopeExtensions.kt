package com.pointlessapps.granite.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val LOADER_DELAY = 100L

internal fun ViewModel.launch(
    onException: (Throwable) -> Unit = { it.printStackTrace() },
    block: suspend CoroutineScope.() -> Unit,
) = viewModelScope.launch(
    context = CoroutineExceptionHandler { _, throwable -> onException(throwable) },
    block = block,
)

internal fun ViewModel.launchWithDelayedLoading(
    onException: (Throwable) -> Unit = { it.printStackTrace() },
    onShowLoader: () -> Unit,
    block: suspend CoroutineScope.() -> Unit,
) = launch(onException) {
    val loader = launch(onException) {
        delay(LOADER_DELAY)
        onShowLoader()
    }

    block()
    loader.cancel()
}
