package com.pointlessapps.obsidian_mini.markdown.renderer.styles.utils

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.ResolvedTextDirection.Ltr

/**
 * Returns a list of bounding boxes that constrain each portion of the text in each line between
 * [startOffset] and [endOffset]. When the text doesn't span until the end of the line (visibly),
 * the bounding box will return it's actual position, not the end of the line.
 */
internal fun TextLayoutResult.getBoundingBoxes(startOffset: Int, endOffset: Int): List<Rect> {
    val startLineNum = getLineForOffset(startOffset)
    val endLineNum = getLineForOffset(endOffset)
    val isLtr = multiParagraph.getParagraphDirection(startOffset) == Ltr

    return (startLineNum..endLineNum).map { lineNum ->
        Rect(
            top = getLineTop(lineNum),
            bottom = getLineBottom(lineNum),
            left = if (lineNum == startLineNum) {
                getHorizontalPosition(startOffset, usePrimaryDirection = isLtr)
            } else {
                getLineLeft(lineNum)
            },
            right = if (lineNum == endLineNum) {
                getHorizontalPosition(endOffset, usePrimaryDirection = isLtr)
            } else {
                getLineRight(lineNum)
            }
        )
    }
}

/**
 * Returns a bounding box that constrain a whole paragraph spanning
 * between [startOffset] and [endOffset].
 */
internal fun TextLayoutResult.getLinesBoundingBox(startOffset: Int, endOffset: Int): Rect {
    val startLineNum = getLineForOffset(startOffset)
    val endLineNum = getLineForOffset(endOffset)

    return Rect(
        top = getLineTop(startLineNum),
        bottom = getLineBottom(endLineNum),
        left = 0f,
        right = size.width.toFloat(),
    )
}

internal fun Rect.inflate(verticalDelta: Float, horizontalDelta: Float) = Rect(
    left = left - horizontalDelta,
    top = top - verticalDelta,
    right = right + horizontalDelta,
    bottom = bottom + verticalDelta,
)
