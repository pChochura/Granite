package com.pointlessapps.granite.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.pointlessapps.granite.R
import com.pointlessapps.granite.home.model.Item
import com.pointlessapps.granite.navigation.Route
import com.pointlessapps.granite.utils.applyIf
import com.pointlessapps.granite.ui.R as RC

@Composable
internal fun BottomBar(
    currentDestination: NavDestination?,
    onNavigateTo: (Route) -> Unit,
    onLongClicked: (Route) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_medium)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BottomBarBackground {
            BottomBarButton(
                bottomBarButton = when (currentDestination?.hasRoute<Route.Editor>()) {
                    true -> BottomBarButton.Active(
                        iconRes = RC.drawable.ic_color_pallete,
                        title = stringResource(R.string.editor),
                    )

                    else -> BottomBarButton.Empty(iconRes = RC.drawable.ic_color_pallete)
                },
                onClicked = { onNavigateTo(Route.Editor()) },
                onLongClicked = { onLongClicked(Route.Editor()) },
            )
            BottomBarButton(
                bottomBarButton = BottomBarButton.Empty(iconRes = RC.drawable.ic_search),
                onClicked = { onNavigateTo(Route.Search) },
                onLongClicked = { onLongClicked(Route.Search) },
            )
        }

        BottomBarSupportingButton(
            iconRes = RC.drawable.ic_today,
            onClicked = { onNavigateTo(Route.DailyNote) },
            onLongClicked = { onLongClicked(Route.DailyNote) }
        )
    }
}

@Composable
internal fun BottomBarBackground(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(
                width = dimensionResource(RC.dimen.default_border_width),
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = CircleShape,
            )
            .padding(dimensionResource(RC.dimen.margin_small)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
internal fun BottomBarButton(
    bottomBarButton: BottomBarButton,
    onClicked: () -> Unit,
    onLongClicked: () -> Unit,
) {
    // TODO support badges
    AnimatedContent(bottomBarButton is BottomBarButton.Active) { isActive ->
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .applyIf(isActive) {
                    background(MaterialTheme.colorScheme.primary)
                }
                .combinedClickable(
                    role = Role.Button,
                    onClick = onClicked,
                    onLongClick = onLongClicked,
                )
                .padding(
                    horizontal = dimensionResource(RC.dimen.margin_medium),
                    vertical = dimensionResource(RC.dimen.margin_small),
                ),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ComposeIcon(
                iconRes = bottomBarButton.iconRes,
                modifier = Modifier.size(dimensionResource(R.dimen.fab_icon_size)),
                iconStyle = defaultComposeIconStyle().copy(
                    tint = if (isActive) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                ),
            )

            if (isActive) {
                ComposeText(
                    text = (bottomBarButton as? BottomBarButton.Active)?.title.orEmpty(),
                    textStyle = defaultComposeTextStyle().copy(
                        textColor = MaterialTheme.colorScheme.onPrimary,
                        typography = MaterialTheme.typography.labelMedium,
                    )
                )
            }
        }
    }
}

@Composable
internal fun BottomBarSupportingButton(
    @DrawableRes iconRes: Int,
    onClicked: () -> Unit,
    onLongClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary)
            .combinedClickable(
                role = Role.Button,
                onClick = onClicked,
                onLongClick = onLongClicked,
            )
            .padding(dimensionResource(RC.dimen.margin_medium)),
        contentAlignment = Alignment.Center,
    ) {
        ComposeIcon(
            iconRes = iconRes,
            modifier = Modifier.size(dimensionResource(R.dimen.fab_icon_size)),
            iconStyle = defaultComposeIconStyle().copy(
                tint = MaterialTheme.colorScheme.onSecondary,
            ),
        )
    }
}

internal sealed class BottomBarButton(
    @DrawableRes open val iconRes: Int,
) {
    data class Active(override val iconRes: Int, val title: String) : BottomBarButton(iconRes)
    data class Empty(override val iconRes: Int) : BottomBarButton(iconRes)
    data class Badge(override val iconRes: Int) : BottomBarButton(iconRes)
}
