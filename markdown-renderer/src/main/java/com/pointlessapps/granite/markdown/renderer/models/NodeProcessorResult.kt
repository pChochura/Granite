package com.pointlessapps.granite.markdown.renderer.models

import androidx.compose.ui.text.AnnotatedString

internal data class NodeProcessorResult(
    val styles: List<AnnotatedString.Range<AnnotatedString.Annotation>>,
    val markers: List<NodeMarker>,
)
