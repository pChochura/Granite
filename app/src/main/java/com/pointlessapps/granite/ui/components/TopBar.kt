package com.pointlessapps.granite.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.granite.ui.R as RC

@Composable
internal fun TopBar(
    @DrawableRes leftIcon: Int,
    @StringRes leftIconTooltip: Int,
    @DrawableRes rightIcon: Int,
    @StringRes rightIconTooltip: Int,
    @StringRes title: Int,
    onLeftIconClicked: () -> Unit,
    onRightIconClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.7f))
            .statusBarsPadding()
            .padding(all = dimensionResource(RC.dimen.margin_medium)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_tiny)),
    ) {
        ComposeIconButton(
            modifier = Modifier.size(dimensionResource(RC.dimen.button_icon_size_big)),
            iconRes = leftIcon,
            tooltipLabel = leftIconTooltip,
            onClick = onLeftIconClicked,
            iconButtonStyle = defaultComposeIconButtonStyle().copy(
                containerColor = Color.Transparent,
                outlineColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )

        ComposeText(
            text = stringResource(title),
            modifier = Modifier.weight(1f),
            textStyle = defaultComposeTextStyle().copy(
                textColor = MaterialTheme.colorScheme.onSurface,
                typography = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            ),
        )

        ComposeIconButton(
            modifier = Modifier.size(dimensionResource(RC.dimen.button_icon_size_big)),
            iconRes = rightIcon,
            tooltipLabel = rightIconTooltip,
            onClick = onRightIconClicked,
            iconButtonStyle = defaultComposeIconButtonStyle().copy(
                containerColor = Color.Transparent,
                outlineColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
    }
}
