package com.project.compose.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.project.R

@Composable
internal fun ComposeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonModel: ComposeButtonModel = defaultComposeButtonModel(),
) {
    Button(
        modifier = modifier,
        elevation = null,
        shape = buttonModel.shape,
        colors = buttonColors(backgroundColor = buttonModel.backgroundColor),
        border = buttonModel.border,
        contentPadding = PaddingValues(
            vertical = buttonModel.verticalPadding,
            horizontal = buttonModel.horizontalPadding,
        ),
        onClick = onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.button_padding_horizontal),
            ),
        ) {
            val iconComposable = @Composable {
                if (buttonModel.iconModel != null) {
                    Icon(
                        painter = painterResource(id = buttonModel.iconModel.icon),
                        tint = Color.White,
                        contentDescription = null,
                    )
                }
            }

            if (buttonModel.iconModel?.position == ComposeButtonIconModel.Position.LEFT) {
                iconComposable()
            }
            ComposeText(
                text = text,
                textStyle = defaultComposeTextStyle().copy(
                    typography = MaterialTheme.typography.body1,
                    textColor = buttonModel.textColor,
                ),
            )
            if (buttonModel.iconModel?.position == ComposeButtonIconModel.Position.RIGHT) {
                iconComposable()
            }
        }
    }
}

@Composable
internal fun defaultComposeButtonModel() = ComposeButtonModel(
    border = null,
    backgroundColor = MaterialTheme.colors.secondary,
    textColor = MaterialTheme.colors.onSecondary,
    shape = MaterialTheme.shapes.medium,
    verticalPadding = dimensionResource(id = R.dimen.button_padding_vertical),
    horizontalPadding = dimensionResource(id = R.dimen.button_padding_horizontal),
    iconModel = null,
)

internal data class ComposeButtonIconModel(
    val position: Position = Position.LEFT,
    @DrawableRes val icon: Int,
) {

    @Suppress("UNUSED")
    enum class Position {
        LEFT, RIGHT,
    }
}

internal data class ComposeButtonModel(
    val border: BorderStroke?,
    val backgroundColor: Color,
    val textColor: Color,
    val shape: Shape,
    val verticalPadding: Dp,
    val horizontalPadding: Dp,
    val iconModel: ComposeButtonIconModel?,
)
