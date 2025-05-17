package com.pointlessapps.obsidian_mini.markdown_renderer.utils

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.util.fastFold
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap

@Stable
class ExtendedSpans(
    vararg painters: ExtendedSpanPainter,
) {
    private val painters = painters.toList()
    internal var drawInstructions = emptyList<SpanDrawInstructions>()

    /**
     * Prepares [text] to be rendered by [painters]. [RoundRectSpanPainter] and [SquigglyUnderlineSpanPainter]
     * use this for removing background and underline spans so that they can be drawn manually.
     */
    fun extend(textFieldValue: TextFieldValue): TextFieldValue {
        with(textFieldValue) {
            return textFieldValue.copy(
                annotatedString = buildAnnotatedString {
                    append(annotatedString.text)
                    addStringAnnotation(
                        EXTENDED_SPANS_MARKER_TAG,
                        annotation = "ignored",
                        start = 0,
                        end = 0,
                    )

                    annotatedString.spanStyles.fastForEach {
                        val decorated = painters.fastFold(initial = it.item) { updated, painter ->
                            painter.decorate(
                                updated,
                                it.start,
                                it.end,
                                text = annotatedString,
                                builder = this,
                            )
                        }
                        addStyle(decorated, it.start, it.end)
                    }
                    annotatedString.paragraphStyles.fastForEach {
                        addStyle(it.item, it.start, it.end)
                    }
                    annotatedString.getStringAnnotations(start = 0, end = text.length).fastForEach {
                        addStringAnnotation(
                            tag = it.tag,
                            annotation = it.item,
                            start = it.start,
                            end = it.end,
                        )
                    }
                    annotatedString.getTtsAnnotations(start = 0, end = text.length).fastForEach {
                        addTtsAnnotation(it.item, it.start, it.end)
                    }
                }
            )
        }
    }

    fun onTextLayout(layoutResult: TextLayoutResult) {
        layoutResult.checkIfExtendWasCalled()
        drawInstructions = painters.fastMap {
            it.drawInstructionsFor(layoutResult)
        }
    }

    private fun TextLayoutResult.checkIfExtendWasCalled() {
        val wasExtendCalled = layoutInput.text.getStringAnnotations(
            tag = EXTENDED_SPANS_MARKER_TAG,
            start = 0,
            end = 0,
        ).isNotEmpty()
        check(wasExtendCalled) {
            "ExtendedSpans#extend(AnnotatedString) wasn't called for this Text()."
        }
    }

    companion object {
        private const val EXTENDED_SPANS_MARKER_TAG = "extended_spans_marker"
    }
}

fun Modifier.drawBehind(spans: ExtendedSpans): Modifier {
    return drawBehind {
        spans.drawInstructions.fastForEach { instructions ->
            with(instructions) {
                draw()
            }
        }
    }
}
