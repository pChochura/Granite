package com.pointlessapps.obsidian_mini.ui_components.utils

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.pointerInput

@Composable
internal fun Modifier.onLongPressOrTap(
    enabled: Boolean,
    duration: Int,
    onPressed: () -> Unit,
    onLongPressed: () -> Unit,
    onCanceled: () -> Unit = {},
): Modifier = composed(
    inspectorInfo = {
        name = "onLongPressOrTap"
        properties["enabled"] = enabled
        properties["duration"] = enabled
        properties["onPressed"] = onPressed
        properties["onLongPressed"] = onLongPressed
        properties["onCanceled"] = onCanceled
    }
) {
    val isEnabledState by rememberUpdatedState(enabled)

    pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown()
            if (isEnabledState) onPressed()
            try {
                withTimeout(duration.toLong()) { waitForUpOrCancellation() }
                if (isEnabledState) onCanceled()
            } catch (_: PointerEventTimeoutCancellationException) {
                if (isEnabledState) onLongPressed()
                waitForUpOrCancellation()?.consume()
            }
        }
    }
}
