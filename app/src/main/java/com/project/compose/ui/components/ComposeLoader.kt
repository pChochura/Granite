package com.project.compose.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.zIndex

private const val SCRIM_ALPHA = 0.9f

@Composable
internal fun ComposeLoader(enabled: Boolean) {
    val focusManager = LocalFocusManager.current
    LaunchedEffect(enabled) {
        if (enabled) {
            focusManager.clearFocus(true)
        }
    }

    AnimatedVisibility(
        modifier = Modifier.zIndex(1f),
        visible = enabled,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Surface(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding(),
            color = MaterialTheme.colors.surface.copy(
                alpha = SCRIM_ALPHA,
            ),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colors.onPrimary,
                )
            }
        }
    }
}
