package com.pointlessapps.obsidian_mini.markdown.renderer

import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.util.fastForEach
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker

// TODO refactor this to save the offsets and reuse them
internal class MarkdownOffsetMapping(private val markers: List<NodeMarker>) : OffsetMapping {

    override fun originalToTransformed(offset: Int): Int {
        var removedLengthBeforeOrAtOffset = 0
        markers.fastForEach { marker ->
            if (offset <= marker.startOffset) {
                return@fastForEach
            }

            if (offset < marker.endOffset) {
                return marker.startOffset - removedLengthBeforeOrAtOffset
            }

            removedLengthBeforeOrAtOffset += marker.endOffset - marker.startOffset
        }

        return offset - removedLengthBeforeOrAtOffset
    }

    override fun transformedToOriginal(offset: Int): Int {
        var addedLength = 0
        markers.fastForEach { marker ->
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
