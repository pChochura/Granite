package com.pointlessapps.granite.markdown.renderer.assist.utils

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.util.fastCoerceAtLeast

internal fun TextRange.offset(offset: Int) = TextRange(
    start = (start + offset).fastCoerceAtLeast(0),
    end = (end + offset).fastCoerceAtLeast(0),
)
