package com.pointlessapps.granite.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.toSize
import com.pointlessapps.granite.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeIconButton(
    @DrawableRes iconRes: Int,
    @StringRes tooltipLabel: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit = {},
    iconButtonStyle: ComposeIconButtonStyle = defaultComposeIconButtonStyle(),
) {
    val interactionSource = remember { MutableInteractionSource() }

    var radius by remember { mutableFloatStateOf(0f) }

    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { PlainTooltip { Text(stringResource(tooltipLabel)) } },
        state = rememberTooltipState(),
    ) {
        ComposeIcon(
            modifier = modifier
                .onGloballyPositioned { radius = it.size.toSize().maxDimension * 0.5f }
                .clip(iconButtonStyle.shape)
                .border(
                    width = dimensionResource(R.dimen.icon_button_border_width),
                    color = if (iconButtonStyle.enabled) {
                        iconButtonStyle.outlineColor
                    } else {
                        iconButtonStyle.disabledOutlineColor
                    },
                    shape = iconButtonStyle.shape,
                )
                .background(
                    if (iconButtonStyle.enabled) {
                        iconButtonStyle.containerColor
                    } else {
                        iconButtonStyle.disabledContainerColor
                    },
                )
                .padding(dimensionResource(R.dimen.margin_small))
                .semantics { role = Role.Button }
                .pointerInput(iconButtonStyle.enabled) {
                    if (!iconButtonStyle.enabled) return@pointerInput

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
                        bounded = false,
                        radius = with(LocalDensity.current) { radius.toDp() },
                    ),
                ),
            iconRes = iconRes,
            iconStyle = defaultComposeIconStyle().copy(
                tint = if (iconButtonStyle.enabled) {
                    iconButtonStyle.contentColor
                } else {
                    iconButtonStyle.disabledContentColor
                },
            ),
        )
    }
}

@Composable
fun defaultComposeIconButtonStyle() = ComposeIconButtonStyle(
    containerColor = MaterialTheme.colorScheme.primary,
    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
    contentColor = MaterialTheme.colorScheme.onPrimary,
    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
    outlineColor = MaterialTheme.colorScheme.primary,
    disabledOutlineColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
    shape = CircleShape,
    enabled = true,
)

data class ComposeIconButtonStyle(
    val containerColor: Color,
    val disabledContainerColor: Color,
    val contentColor: Color,
    val disabledContentColor: Color,
    val outlineColor: Color,
    val disabledOutlineColor: Color,
    val shape: Shape,
    val enabled: Boolean,
)
