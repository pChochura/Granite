package com.pointlessapps.obsidian_mini

import androidx.compose.ui.text.input.OffsetMapping
import com.pointlessapps.obsidian_mini.models.NodeMarker

internal class MarkdownOffsetMapping(private val markers: List<NodeMarker>) : OffsetMapping {
    private val cumulativeRemovedLengths = IntArray(markers.size + 1)
    private val originalMarkerStarts = IntArray(markers.size)
    private val transformedMarkerStarts = IntArray(markers.size)

    init {
        var currentRemovedLength = 0
        markers.forEachIndexed { index, marker ->
            cumulativeRemovedLengths[index] = currentRemovedLength
            originalMarkerStarts[index] = marker.startOffset
            transformedMarkerStarts[index] = marker.startOffset - currentRemovedLength

            currentRemovedLength += (marker.endOffset - marker.startOffset)
        }
        cumulativeRemovedLengths[markers.size] = currentRemovedLength
    }

    override fun originalToTransformed(offset: Int): Int {
        var removedLengthBeforeOrAtOffset = 0
        for (marker in markers) {
            if (offset <= marker.startOffset) {
                break
            }

            if (offset < marker.endOffset) {
                return marker.startOffset - removedLengthBeforeOrAtOffset
            }

            removedLengthBeforeOrAtOffset += (marker.endOffset - marker.startOffset)
        }

        return offset - removedLengthBeforeOrAtOffset
    }

    override fun transformedToOriginal(offset: Int): Int {
        var addedLength = 0
        for (marker in markers) {
            val markerOriginalLength = marker.endOffset - marker.startOffset
            val markerTransformedStartInOriginalContext = marker.startOffset - addedLength

            if (offset <= markerTransformedStartInOriginalContext) {
                return offset + addedLength
            }

            addedLength += markerOriginalLength
        }

        return offset + addedLength
    }
}
