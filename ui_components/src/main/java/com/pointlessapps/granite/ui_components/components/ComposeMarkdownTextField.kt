package com.pointlessapps.granite.ui_components.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.pointlessapps.granite.markdown.renderer.MarkdownTransformation
import com.pointlessapps.granite.markdown.renderer.styles.draw
import com.pointlessapps.granite.markdown.renderer.styles.rememberMarkdownSpanStyles
import com.pointlessapps.granite.ui_components.R

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

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
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

        Spacer(Modifier.height(dimensionResource(R.dimen.bottom_markdown_padding)))
    }
}
