package com.pointlessapps.granite.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import com.pointlessapps.granite.ui.R

@Composable
fun ComposeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    chipStyle: ComposeChipStyle = defaultComposeChipStyle(),
) {
    Row(
        modifier = modifier
            .fillMaxHeight()
            .clip(CircleShape)
            .background(
                if (isSelected) {
                    chipStyle.selectedContainerColor
                } else {
                    chipStyle.containerColor
                }
            )
            .border(
                width = dimensionResource(R.dimen.chip_border_width),
                color = if (isSelected) {
                    chipStyle.selectedOutlineColor
                } else {
                    chipStyle.outlineColor
                },
                shape = CircleShape,
            )
            .clickable(
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(
                horizontal = dimensionResource(R.dimen.margin_tiny),
                vertical = dimensionResource(R.dimen.margin_nano),
            )
            .animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_nano)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isSelected) {
            ComposeIcon(
                modifier = Modifier.size(dimensionResource(R.dimen.chip_icon_size)),
                iconRes = R.drawable.ic_done,
                iconStyle = defaultComposeIconStyle().copy(
                    tint = chipStyle.iconColor,
                ),
            )
        }
        ComposeText(
            modifier = Modifier
                .fillMaxSize()
                .sizeIn(
                    minHeight = dimensionResource(R.dimen.chip_icon_size),
                ),
            text = label,
            textStyle = defaultComposeTextStyle().copy(
                typography = MaterialTheme.typography.labelLarge,
                textColor = if (isSelected) {
                    chipStyle.selectedLabelColor
                } else {
                    chipStyle.labelColor
                },
            ),
        )
    }
}

@Composable
fun defaultComposeChipStyle() = ComposeChipStyle(
    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
    outlineColor = MaterialTheme.colorScheme.outlineVariant,
    selectedOutlineColor = MaterialTheme.colorScheme.secondaryContainer,
    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
    iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
    iconRes = R.drawable.ic_done,
)

data class ComposeChipStyle(
    val containerColor: Color,
    val selectedContainerColor: Color,
    val outlineColor: Color,
    val selectedOutlineColor: Color,
    val labelColor: Color,
    val selectedLabelColor: Color,
    val iconColor: Color,
    @DrawableRes val iconRes: Int,
)
