package com.pointlessapps.obsidian_mini.markdown.renderer.styles

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult

interface MarkdownSpanStyle {
    fun prepare(result: TextLayoutResult, text: AnnotatedString): DrawInstruction

    fun interface DrawInstruction {
        fun DrawScope.draw()
    }
}
