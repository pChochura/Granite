package com.pointlessapps.obsidian_mini.markdown.renderer.models

import androidx.compose.ui.text.AnnotatedString

internal data class NodeProcessorResult(
    val styles: List<AnnotatedString.Range<AnnotatedString.Annotation>>,
    val markers: List<NodeMarker>,
)
