package com.pointlessapps.obsidian_mini.markdown.renderer.models

internal data class NodeProcessorResult(
    val styles: List<NodeStyle>,
    val markers: List<NodeMarker>,
)
