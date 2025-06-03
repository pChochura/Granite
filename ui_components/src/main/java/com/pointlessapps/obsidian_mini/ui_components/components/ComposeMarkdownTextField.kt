package com.pointlessapps.obsidian_mini.ui_components.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.pointlessapps.obsidian_mini.markdown.renderer.MarkdownTransformation
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.draw
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.rememberMarkdownSpanStyles

@Composable
fun ComposeMarkdownTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    onImeAction: ((ImeAction) -> Unit)? = null,
    textFieldStyle: ComposeTextFieldStyle = defaultComposeTextFieldStyle(),
) {
    val markdownTransformation by remember { mutableStateOf(MarkdownTransformation(value.selection)) }
    val markdownSpanStyles = rememberMarkdownSpanStyles()

    ComposeTextField(
        modifier = modifier.draw(markdownSpanStyles),
        value = value,
        onValueChange = onValueChange,
        onImeAction = onImeAction,
        onTextLayout = { result ->
            markdownSpanStyles.update(
                result = result,
                text = markdownTransformation.withSelection(value.selection)
                    .filter(value.annotatedString).text,
            )
        },
        textFieldStyle = textFieldStyle.copy(
            visualTransformation = remember(value.selection) {
                markdownTransformation.withSelection(value.selection)
            },
        ),
    )
}
