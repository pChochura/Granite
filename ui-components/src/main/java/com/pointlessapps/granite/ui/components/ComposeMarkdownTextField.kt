package com.pointlessapps.granite.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import com.pointlessapps.granite.markdown.renderer.MarkdownTransformation
import com.pointlessapps.granite.markdown.renderer.styles.draw
import com.pointlessapps.granite.markdown.renderer.styles.rememberMarkdownSpanStyles
import com.pointlessapps.granite.markdown.renderer.styles.spans.CodeBlockMarkdownSpanStyle
import com.pointlessapps.granite.ui.R

@Composable
fun ComposeMarkdownTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    onImeAction: ((ImeAction) -> Unit)? = null,
    onRunCodeBlock: ((String) -> Unit)? = null,
    textFieldStyle: ComposeTextFieldStyle = defaultComposeTextFieldStyle(),
) {
    val markdownTransformation by remember { mutableStateOf(MarkdownTransformation(value.selection)) }
    val markdownSpanStyles = rememberMarkdownSpanStyles()

    val transformation by remember(value.selection) {
        mutableStateOf(markdownTransformation.withSelection(value.selection))
    }
    val transformedValue by remember(transformation, value) {
        mutableStateOf(transformation.filter(value.annotatedString).text)
    }

    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val codeBlocksAnnotations by remember(textLayoutResult, transformedValue) {
        mutableStateOf(
            transformedValue.getStringAnnotations(
                // Get only the code blocks annotations with Mica lang
                tag = "${CodeBlockMarkdownSpanStyle.TAG_CONTENT}_mica",
                start = 0,
                end = transformedValue.length,
            )
        )
    }

    Box(modifier = Modifier.wrapContentSize()) {
        ComposeTextField(
            modifier = modifier.draw(markdownSpanStyles),
            value = value,
            onValueChange = onValueChange,
            onImeAction = onImeAction,
            onTextLayout = { result ->
                textLayoutResult = result
                markdownSpanStyles.update(result, transformedValue)
            },
            textFieldStyle = textFieldStyle.copy(visualTransformation = transformation),
        )

        if (textLayoutResult != null) {
            codeBlocksAnnotations.forEach { annotation ->
                ComposeIcon(
                    modifier = Modifier
                        .padding(
                            vertical = dimensionResource(R.dimen.margin_tiny),
                            horizontal = dimensionResource(R.dimen.margin_semi_big),
                        )
                        .align(Alignment.TopEnd)
                        .offset {
                            textLayoutResult?.let {
                                IntOffset(
                                    x = 0,
                                    y = it.getLineTop(
                                        it.getLineForOffset(annotation.start),
                                    ).toInt(),
                                )
                            } ?: IntOffset.Zero
                        }
                        .clip(CircleShape)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = stringResource(R.string.run),
                            onClick = {
                                onRunCodeBlock?.invoke(
                                    transformedValue.substring(annotation.start, annotation.end)
                                )
                            },
                        )
                        .size(dimensionResource(R.dimen.code_block_icon_size)),
                    iconRes = R.drawable.ic_play,
                    contentDescription = stringResource(R.string.run),
                    iconStyle = defaultComposeIconStyle().copy(
                        tint = MaterialTheme.colorScheme.secondary,
                    ),
                )
            }
        }
    }
}
