package com.pointlessapps.granite.markdown.renderer.models

import androidx.compose.ui.text.AnnotatedString

internal data class AccumulateStylesResult(
    val styles: List<AnnotatedString.Range<AnnotatedString.Annotation>>,
    val markers: List<NodeMarker>,
)
