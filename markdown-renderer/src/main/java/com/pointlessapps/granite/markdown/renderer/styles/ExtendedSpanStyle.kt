package com.pointlessapps.granite.markdown.renderer.styles

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp

sealed class ExtendedSpanStyle {
    internal abstract class Default<out T : ExtendedSpanStyle> {
        @Composable
        abstract fun defaultStyle(): T
    }
}

data class HighlightSpanStyle(
    val backgroundColor: Color,
    val cornerRadius: Float,
    val padding: Float,
) : ExtendedSpanStyle() {

    internal companion object : Default<HighlightSpanStyle>() {
        @Composable
        override fun defaultStyle() = with(LocalDensity.current) {
            HighlightSpanStyle(
                backgroundColor = Color(255, 221, 114, 255),
                cornerRadius = 4.sp.toPx(),
                padding = 2.sp.toPx(),
            )
        }
    }
}

data class BlockQuoteSpanStyle(
    val backgroundColor: Color,
    val cornerRadius: Float,
    val width: Float,
    val horizontalPadding: Float,
    val verticalPadding: Float,
) : ExtendedSpanStyle() {
    internal companion object : Default<BlockQuoteSpanStyle>() {
        @Composable
        override fun defaultStyle() = with(LocalDensity.current) {
            BlockQuoteSpanStyle(
                backgroundColor = Color(51, 51, 51),
                cornerRadius = 4.sp.toPx(),
                width = 2.sp.toPx(),
                horizontalPadding = 12.sp.toPx(),
                verticalPadding = 4.sp.toPx(),
            )
        }
    }
}

data class CodeBlockSpanStyle(
    val backgroundColor: Color,
    val backgroundCornerRadius: Float,
    val backgroundPadding: Float,
) : ExtendedSpanStyle() {
    internal companion object : Default<CodeBlockSpanStyle>() {
        @Composable
        override fun defaultStyle() = with(LocalDensity.current) {
            CodeBlockSpanStyle(
                backgroundColor = Color(59, 59, 59, 128),
                backgroundCornerRadius = 4.sp.toPx(),
                backgroundPadding = 4.sp.toPx(),
            )
        }
    }
}

data class CodeSpanSpanStyle(
    val backgroundColor: Color,
    val cornerRadius: Float,
    val padding: Float,
) : ExtendedSpanStyle() {
    internal companion object : Default<CodeSpanSpanStyle>() {
        @Composable
        override fun defaultStyle() = with(LocalDensity.current) {
            CodeSpanSpanStyle(
                backgroundColor = Color(59, 59, 59, 128),
                cornerRadius = 4.sp.toPx(),
                padding = 2.sp.toPx(),
            )
        }
    }
}

data class HashtagSpanStyle(
    val backgroundColor: Color,
    val cornerRadius: Float,
    val horizontalPadding: Float,
    val verticalPadding: Float,
) : ExtendedSpanStyle() {
    internal companion object : Default<HashtagSpanStyle>() {
        @Composable
        override fun defaultStyle() = with(LocalDensity.current) {
            HashtagSpanStyle(
                backgroundColor = Color(76, 175, 80),
                cornerRadius = 16.sp.toPx(),
                horizontalPadding = 4.sp.toPx(),
                verticalPadding = 2.sp.toPx(),
            )
        }
    }
}

data class HorizontalRuleSpanStyle(
    val backgroundColor: Color,
    val cornerRadius: Float,
    val height: Float,
) : ExtendedSpanStyle() {
    internal companion object : Default<HorizontalRuleSpanStyle>() {
        @Composable
        override fun defaultStyle() = with(LocalDensity.current) {
            HorizontalRuleSpanStyle(
                backgroundColor = Color(51, 51, 51),
                cornerRadius = 16.sp.toPx(),
                height = 4.sp.toPx(),
            )
        }
    }
}

data class CalloutSpanStyle(
    val backgroundColor: Color,
    val cornerRadius: Float,
    val width: Float,
    val horizontalPadding: Float,
    val verticalPadding: Float,
) : ExtendedSpanStyle() {
    internal companion object : Default<CalloutSpanStyle>() {
        @Composable
        override fun defaultStyle() = with(LocalDensity.current) {
            CalloutSpanStyle(
                backgroundColor = Color(51, 51, 51),
                cornerRadius = 4.sp.toPx(),
                width = 2.sp.toPx(),
                horizontalPadding = 12.sp.toPx(),
                verticalPadding = 8.sp.toPx(),
            )
        }
    }
}
