package com.pointlessapps.obsidian_mini.markdown.renderer.models

/**
 * Indicates a region that is removed while the node holding it is not in focus.
 * Additionally it can be replaced by a [replacement] instead of removing the content.
 */
data class NodeMarker(
    val startOffset: Int,
    val endOffset: Int,
    val replacement: String? = null,
)
