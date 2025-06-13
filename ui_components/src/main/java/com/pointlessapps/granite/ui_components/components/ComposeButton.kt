package com.pointlessapps.granite.ui_components.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.toSize
import com.pointlessapps.granite.ui_components.R

@Composable
fun ComposeButton(
    label: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    onLongClick: () -> Unit = {},
    buttonStyle: ComposeButtonStyle = defaultComposeButtonStyle(),
) {
    val interactionSource = remember { MutableInteractionSource() }

    var radius by remember { mutableFloatStateOf(0f) }

    Row(
        modifier = modifier
            .onGloballyPositioned { radius = it.size.toSize().maxDimension * 0.5f }
            .defaultMinSize(
                minWidth = ButtonDefaults.MinWidth,
                minHeight = ButtonDefaults.MinHeight,
            )
            .clip(buttonStyle.shape)
            .background(
                color = if (buttonStyle.enabled) {
                    buttonStyle.containerColor
                } else {
                    buttonStyle.disabledContainerColor
                },
                shape = buttonStyle.shape,
            )
            .pointerInput(buttonStyle.enabled) {
                if (!buttonStyle.enabled) return@pointerInput

                detectTapGestures(
                    onPress = {
                        val press = PressInteraction.Press(it)
                        interactionSource.emit(press)
                        if (tryAwaitRelease()) {
                            interactionSource.emit(PressInteraction.Release(press))
                        } else {
                            interactionSource.emit(PressInteraction.Cancel(press))
                        }
                    },
                    onTap = { onClick() },
                    onLongPress = { onLongClick() },
                )
            }
            .indication(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    radius = with(LocalDensity.current) { radius.toDp() },
                ),
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val content = @Composable {
            if (buttonStyle.iconRes != null) {
                ComposeIcon(
                    iconRes = buttonStyle.iconRes,
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.button_icon_size))
                        .then(iconModifier),
                    iconStyle = defaultComposeIconStyle().copy(
                        tint = if (buttonStyle.enabled) {
                            buttonStyle.textStyle.textColor
                        } else {
                            buttonStyle.textStyle.disabledTextColor
                        },
                    ),
                )
            }

            if (label != null) {
                ComposeText(
                    text = label,
                    textStyle = buttonStyle.textStyle.copy(
                        textColor = if (buttonStyle.enabled) {
                            buttonStyle.textStyle.textColor
                        } else {
                            buttonStyle.textStyle.disabledTextColor
                        },
                    ),
                )
            }
        }

        when (buttonStyle.orientation) {
            ComposeButtonOrientation.Vertical -> Column(
                modifier = Modifier.padding(
                    vertical = dimensionResource(R.dimen.margin_small),
                    horizontal = dimensionResource(R.dimen.margin_medium),
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.margin_small),
                ),
            ) {
                content()
            }

            ComposeButtonOrientation.Horizontal -> Row(
                modifier = Modifier.padding(
                    vertical = dimensionResource(R.dimen.margin_small),
                    horizontal = dimensionResource(R.dimen.margin_medium),
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.margin_small),
                ),
            ) {
                content()
            }
        }
    }
}

@Composable
fun defaultComposeButtonStyle() = ComposeButtonStyle(
    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    disabledContainerColor = MaterialTheme.colorScheme.onSurface,
    shape = CircleShape,
    iconRes = R.drawable.ic_settings,
    orientation = ComposeButtonOrientation.Horizontal,
    textStyle = defaultComposeButtonTextStyle(),
    enabled = true,
)

@Composable
fun defaultComposeButtonTextStyle() = defaultComposeTextStyle().copy(
    textColor = MaterialTheme.colorScheme.primary,
    typography = MaterialTheme.typography.labelLarge,
)

data class ComposeButtonStyle(
    val containerColor: Color,
    val disabledContainerColor: Color,
    val shape: Shape,
    @DrawableRes val iconRes: Int?,
    val orientation: ComposeButtonOrientation,
    val textStyle: ComposeTextStyle,
    val enabled: Boolean,
)

enum class ComposeButtonOrientation { Vertical, Horizontal }
