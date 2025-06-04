package com.pointlessapps.markdown.obsidian.parser.obsidian

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementType

object ObsidianTokenTypes {
    @JvmField
    val EQ: IElementType = MarkdownElementType("=", true)

    @JvmField
    val PERCENT: IElementType = MarkdownElementType("%", true)

    @JvmField
    val HASH: IElementType = MarkdownElementType("#", true)

    @JvmField
    val CARET: IElementType = MarkdownElementType("^", true)

    @JvmField
    val PIPE: IElementType = MarkdownElementType("|", true)

    @JvmField
    val FOOTNOTE_DEFINITION: IElementType = MarkdownElementType("FOOTNOTE_DEFINITION", true)

    @JvmField
    val HASHTAG_TEXT: IElementType = MarkdownElementType("HASHTAG_TEXT", true)

    @JvmField
    val BLOCK_ID: IElementType = MarkdownElementType("BLOCK_ID", true)
}

object ObsidianElementTypes {
    @JvmField
    val HIGHLIGHT: IElementType = MarkdownElementType("HIGHLIGHT")

    @JvmField
    val HASHTAG: IElementType = MarkdownElementType("HASHTAG")

    @JvmField
    val COMMENT: IElementType = MarkdownElementType("COMMENT")

    @JvmField
    val COMMENT_BLOCK: IElementType = MarkdownElementType("COMMENT_BLOCK")

    @JvmField
    val COMMENT_BLOCK_CONTENT: IElementType = MarkdownElementType("COMMENT_BLOCK_CONTENT")

    @JvmField
    val BLOCK_QUOTE_CONTENT: IElementType = MarkdownElementType("BLOCK_QUOTE_CONTENT")

    @JvmField
    val CALLOUT: IElementType = MarkdownElementType("CALLOUT")

    @JvmField
    val CALLOUT_TYPE: IElementType = MarkdownElementType("CALLOUT_TYPE")

    @JvmField
    val CALLOUT_TITLE: IElementType = MarkdownElementType("CALLOUT_TITLE")

    @JvmField
    val INTERNAL_LINK: IElementType = MarkdownElementType("INTERNAL_LINK")

    @JvmField
    val EMBED: IElementType = MarkdownElementType("EMBED")

    @JvmField
    val FOOTNOTE_LINK: IElementType = MarkdownElementType("FOOTNOTE_LINK")

    @JvmField
    val INLINE_FOOTNOTE: IElementType = MarkdownElementType("INLINE_FOOTNOTE")

    @JvmField
    val FOOTNOTE_ID: IElementType = MarkdownElementType("FOOTNOTE_ID")

    @JvmField
    val FOOTNOTE_DEFINITION: IElementType = MarkdownElementType("FOOTNOTE_DEFINITION")

    @JvmField
    val FOOTNOTE_DEFINITION_TEXT: IElementType = MarkdownElementType("FOOTNOTE_DEFINITION_TEXT")

    @JvmField
    val BLOCK_ID: IElementType = MarkdownElementType("BLOCK_ID")
}
