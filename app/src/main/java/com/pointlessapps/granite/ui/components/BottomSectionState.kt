package com.pointlessapps.granite.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.platform.LocalDensity

internal class BottomSectionState {
    val currentHeight = mutableIntStateOf(0)

    val asPaddingValues
        @Composable get() = with(LocalDensity.current) {
            PaddingValues(bottom = currentHeight.intValue.toDp())
        }
}
