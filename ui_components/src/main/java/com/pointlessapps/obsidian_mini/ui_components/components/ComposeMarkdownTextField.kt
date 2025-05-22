package com.pointlessapps.obsidian_mini.ui_components.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.pointlessapps.obsidian_mini.markdown.renderer.MarkdownTransformation

@Composable
fun ComposeMarkdownTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    onImeAction: ((ImeAction) -> Unit)? = null,
    textFieldStyle: ComposeTextFieldStyle = defaultComposeTextFieldStyle(),
) {
    val markdownTransformation by remember { mutableStateOf(MarkdownTransformation(value.selection)) }

    ComposeTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        onImeAction = onImeAction,
        textFieldStyle = textFieldStyle.copy(
            visualTransformation = remember(value.selection) {
                markdownTransformation.withSelection(value.selection)
            },
        ),
    )
}
