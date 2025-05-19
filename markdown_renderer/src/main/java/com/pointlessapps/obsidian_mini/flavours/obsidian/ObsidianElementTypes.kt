package com.pointlessapps.obsidian_mini.flavours.obsidian

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementType

object ObsidianTokenTypes {
    @JvmField
    val EQ: IElementType = MarkdownElementType("=", true)

    @JvmField
    val CARET: IElementType = MarkdownElementType("^", true)

    @JvmField
    val FOOTNOTE_DEFINITION: IElementType = MarkdownElementType("FOOTNOTE_DEFINITION", true)
}

object ObsidianElementTypes {
    @JvmField
    val HIGHLIGHT: IElementType = MarkdownElementType("HIGHLIGHT")

    @JvmField
    val INTERNAL_LINK: IElementType = MarkdownElementType("INTERNAL_LINK")

    @JvmField
    val FOOTNOTE_LINK: IElementType = MarkdownElementType("FOOTNOTE_LINK")

    @JvmField
    val FOOTNOTE_ID: IElementType = MarkdownElementType("FOOTNOTE_ID")

    @JvmField
    val FOOTNOTE_DEFINITION: IElementType = MarkdownElementType("FOOTNOTE_DEFINITION")

    @JvmField
    val FOOTNOTE_DEFINITION_TEXT: IElementType = MarkdownElementType("FOOTNOTE_DEFINITION_TEXT")
}
