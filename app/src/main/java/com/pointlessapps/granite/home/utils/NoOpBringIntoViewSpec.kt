package com.pointlessapps.granite.home.utils

import androidx.compose.foundation.gestures.BringIntoViewSpec

internal val NoOpBringIntoViewSpec = object : BringIntoViewSpec {
    override fun calculateScrollDistance(offset: Float, size: Float, containerSize: Float) = 0f
}
