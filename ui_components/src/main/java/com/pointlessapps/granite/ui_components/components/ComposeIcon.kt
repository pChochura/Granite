package com.pointlessapps.granite.ui_components.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

@Composable
fun ComposeIcon(
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    iconStyle: ComposeIconStyle = defaultComposeIconStyle(),
) = Icon(
    painter = painterResource(iconRes),
    modifier = modifier,
    tint = iconStyle.tint,
    contentDescription = contentDescription,
)

@Composable
fun defaultComposeIconStyle() = ComposeIconStyle(
    tint = MaterialTheme.colorScheme.onSurface,
)

data class ComposeIconStyle(
    val tint: Color,
)
