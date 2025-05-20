package com.pointlessapps.obsidian_mini.models

internal data class NodeProcessorResult(
    val styles: Collection<NodeStyle>,
    val markers: Collection<NodeMarker>,
    val processChildren: Boolean,
)
