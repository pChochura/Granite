package com.pointlessapps.granite.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.IntOffset
import com.pointlessapps.granite.markdown.renderer.MarkdownTransformation
import com.pointlessapps.granite.markdown.renderer.processors.CodeBlockProcessor
import com.pointlessapps.granite.markdown.renderer.styles.draw
import com.pointlessapps.granite.markdown.renderer.styles.rememberMarkdownSpanStyles
import com.pointlessapps.granite.ui.R
import kotlinx.coroutines.FlowPreview

@Composable
fun ComposeMarkdownTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    onTransformedTextChange: (TransformedText) -> Unit = {},
    onRunCodeBlock: ((String) -> Unit)? = null,
    textFieldStyle: ComposeTextFieldStyle = defaultComposeTextFieldStyle(),
) {
    val markdownTransformation by remember { mutableStateOf(MarkdownTransformation()) }
    val markdownSpanStyles = rememberMarkdownSpanStyles()

    val transformation by remember(value) {
        mutableStateOf(markdownTransformation.withSelection(value.selection))
    }
    val transformedText by remember(transformation, value) {
        mutableStateOf(transformation.filter(value.annotatedString))
    }

    LaunchedEffect(transformedText) {
        onTransformedTextChange(transformedText)
    }

    var codeBlocksAnnotations: Map<AnnotatedString.Range<String>, Float> by remember {
        mutableStateOf(emptyMap())
    }

    Box {
        ComposeTextField(
            modifier = modifier.draw(markdownSpanStyles),
            value = value,
            onValueChange = onValueChange,
            onTextLayout = { result ->
                markdownSpanStyles.update(result, transformedText.text)
                codeBlocksAnnotations = transformedText.text.getStringAnnotations(
                    tag = CodeBlockProcessor.TAG_LANG,
                    start = 0,
                    end = transformedText.text.length,
                ).filter { it.item == "mica" }.associateWith {
                    result.getLineTop(result.getLineForOffset(it.start))
                }
            },
            textFieldStyle = textFieldStyle.copy(visualTransformation = transformation),
        )

        codeBlocksAnnotations.forEach { annotation ->
            RunMicaButton(
                transformedValue = transformedText.text,
                annotation = annotation,
                onRunCodeBlock = onRunCodeBlock,
            )
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun BoxScope.RunMicaButton(
    transformedValue: AnnotatedString,
    annotation: Map.Entry<AnnotatedString.Range<String>, Float>,
    onRunCodeBlock: ((String) -> Unit)?,
) {
    ComposeIcon(
        modifier = Modifier
            .padding(
                vertical = dimensionResource(R.dimen.margin_tiny),
                horizontal = dimensionResource(R.dimen.margin_semi_big),
            )
            .align(Alignment.TopEnd)
            .offset { IntOffset(x = 0, y = annotation.value.toInt()) }
            .clip(CircleShape)
            .clickable(
                role = Role.Button,
                onClickLabel = stringResource(R.string.run),
                onClick = {
                    onRunCodeBlock?.invoke(
                        transformedValue.substring(annotation.key.start, annotation.key.end)
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
