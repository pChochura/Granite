package com.pointlessapps.obsidian_mini.markdown.renderer.models

import androidx.compose.ui.text.ParagraphStyle

sealed class NodeType {
    /**
     * Typically a label from the link or an image:
     * `![label](link)`, `[label](link)`, `[[link|label]]`.
     */
    data object Label : NodeType()

    /**
     * In case of a clickable element, a [data] that can be used as a link that was clicked.
     */
    data class Data(val data: String) : NodeType()

    /**
     * Content that does not qualify as decoration.
     */
    data object Content : NodeType()

    /**
     * Decoration that is removed in most cases while rendering:
     * `==highlight==`, `~~strikethrough~~`, `![[embed]]`.
     */
    data object Decoration : NodeType()

    /**
     * A whole node including a newline character. Styling this as a [ParagraphStyle]
     * renders the cursor at the end of the line more smoothly.
     */
    data object Paragraph : NodeType()

    /**
     * A whole node without the newline character.
     */
    data object All : NodeType()
}
