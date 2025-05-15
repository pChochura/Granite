package com.pointlessapps.obsidian_mini.ui_components.components

import android.text.Html
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.project.ui_components.R
import com.pointlessapps.obsidian_mini.ui_components.utils.toAnnotatedString

@Composable
fun ComposeText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: ComposeTextStyle = defaultComposeTextStyle(),
) = ComposeText(
    text = AnnotatedString(text),
    modifier = modifier,
    textStyle = textStyle,
)

@Composable
fun ComposeHTMLText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: ComposeTextStyle = defaultComposeTextStyle(),
    urlSpanStyle: SpanStyle = defaultComposeTextUrlSpanStyle(),
) = ComposeText(
    text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        .toAnnotatedString(urlSpanStyle = urlSpanStyle),
    modifier = modifier,
    textStyle = textStyle,
)

@Composable
fun ComposeText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    textStyle: ComposeTextStyle = defaultComposeTextStyle(),
) = Text(
    modifier = modifier,
    text = text,
    style = textStyle.typography.copy(
        color = textStyle.textColor,
        textAlign = textStyle.textAlign,
    ),
    overflow = textStyle.textOverflow,
    maxLines = textStyle.maxLines,
)

@Composable
fun defaultComposeTextStyle() = ComposeTextStyle(
    textColor = MaterialTheme.colorScheme.onSurface,
    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
    typography = MaterialTheme.typography.bodyMedium,
    textAlign = TextAlign.Start,
    textOverflow = TextOverflow.Visible,
    maxLines = Int.MAX_VALUE,
)

@Composable
fun defaultComposeTextUrlSpanStyle() = SpanStyle(
    color = colorResource(R.color.pink),
    textDecoration = TextDecoration.Underline,
)

data class ComposeTextStyle(
    val textColor: Color,
    val disabledTextColor: Color,
    val textAlign: TextAlign,
    val typography: TextStyle,
    val textOverflow: TextOverflow,
    val maxLines: Int,
)
