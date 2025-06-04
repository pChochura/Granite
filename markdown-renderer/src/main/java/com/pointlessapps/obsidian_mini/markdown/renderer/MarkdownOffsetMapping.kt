package com.pointlessapps.obsidian_mini.markdown.renderer

import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.util.fastForEach
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker

internal class MarkdownOffsetMapping(
    private val markers: List<NodeMarker>,
    private val originalTextLength: Int,
) : OffsetMapping {

    private val originalToTransformedMap = mutableMapOf<Int, Int>()
    private val transformedToOriginalMap = mutableMapOf<Int, Int>()

    override fun originalToTransformed(offset: Int): Int {
        originalToTransformedMap[offset]?.let { return it }

        var removedLengthBeforeOrAtOffset = 0
        markers.fastForEach { marker ->
            if (offset <= marker.startOffset) {
                return@fastForEach
            }

            if (offset < marker.endOffset) {
                return (marker.startOffset - removedLengthBeforeOrAtOffset)
                    .also { originalToTransformedMap[offset] = it }
            }

            val markerLength = marker.endOffset - marker.startOffset
            val markerReplacementLength = marker.replacement?.length ?: 0
            removedLengthBeforeOrAtOffset += markerLength - markerReplacementLength
        }

        return (offset - removedLengthBeforeOrAtOffset)
            .also { originalToTransformedMap[offset] = it }
    }

    override fun transformedToOriginal(offset: Int): Int {
        transformedToOriginalMap[offset]?.let { return it }

        var accumulatedOriginalLength = 0
        var accumulatedTransformedLength = 0

        markers.fastForEach { marker ->
            val markerOriginalLength = marker.endOffset - marker.startOffset
            val markerReplacementLength = marker.replacement?.length ?: 0

            val markerTransformedStart =
                marker.startOffset - accumulatedOriginalLength + accumulatedTransformedLength

            if (offset < markerTransformedStart) {
                return (offset + accumulatedOriginalLength - accumulatedTransformedLength)
                    .coerceIn(0, originalTextLength).also { transformedToOriginalMap[offset] = it }
            }

            if (offset < markerTransformedStart + markerReplacementLength) {
                return marker.startOffset.coerceIn(0, originalTextLength)
                    .also { transformedToOriginalMap[offset] = it }
            }

            accumulatedOriginalLength += markerOriginalLength
            accumulatedTransformedLength += markerReplacementLength
        }

        return (offset + accumulatedOriginalLength - accumulatedTransformedLength)
            .coerceIn(0, originalTextLength).also { transformedToOriginalMap[offset] = it }
    }
}
