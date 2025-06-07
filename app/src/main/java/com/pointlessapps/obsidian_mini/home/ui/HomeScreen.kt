package com.pointlessapps.obsidian_mini.home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.pointlessapps.obsidian_mini.R
import com.pointlessapps.obsidian_mini.navigation.Route
import com.pointlessapps.obsidian_mini.ui_components.components.ComposeIconButton
import com.pointlessapps.obsidian_mini.ui_components.components.ComposeMarkdownTextField
import com.pointlessapps.obsidian_mini.ui_components.components.ComposeScaffoldLayout
import com.pointlessapps.obsidian_mini.ui_components.components.ComposeText
import com.pointlessapps.obsidian_mini.ui_components.components.defaultComposeIconButtonStyle
import com.pointlessapps.obsidian_mini.ui_components.components.defaultComposeTextFieldStyle
import com.pointlessapps.obsidian_mini.ui_components.components.defaultComposeTextStyle
import org.koin.androidx.compose.koinViewModel
import com.pointlessapps.obsidian_mini.ui_components.R as RC


@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateTo: (Route) -> Unit,
) {
    ComposeScaffoldLayout(
        topBar = { TopBar() },
        fab = { BottomBar({}) },
    ) {
        ComposeMarkdownTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(RC.dimen.margin_semi_big),
                    vertical = dimensionResource(RC.dimen.margin_tiny),
                )
                .padding(it),
            value = viewModel.state.textValue,
            onValueChange = viewModel::onTextValueChanged,
            textFieldStyle = defaultComposeTextFieldStyle(),
        )
    }
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            .statusBarsPadding()
            .padding(all = dimensionResource(RC.dimen.margin_medium)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_tiny)),
    ) {
        ComposeIconButton(
            iconRes = RC.drawable.ic_arrow_left,
            onClick = {},
            iconButtonStyle = defaultComposeIconButtonStyle().copy(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                outlineColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
        ComposeText(
            text = stringResource(R.string.title_note),
            modifier = Modifier.weight(1f),
            textStyle = defaultComposeTextStyle().copy(
                textColor = MaterialTheme.colorScheme.onSurface,
                typography = MaterialTheme.typography.headlineSmall,
            ),
        )

        ComposeIconButton(
            iconRes = RC.drawable.ic_warning,
            onClick = {},
            iconButtonStyle = defaultComposeIconButtonStyle().copy(
                containerColor = Color.Transparent,
                outlineColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BottomBar(insertStyle: () -> Unit) {
    AnimatedVisibility(
        visible = WindowInsets.isImeVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(
                        topStart = dimensionResource(RC.dimen.medium_rounded_corners),
                        topEnd = dimensionResource(RC.dimen.medium_rounded_corners),
                    ),
                )
                .navigationBarsPadding()
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_tiny)),
            contentPadding = PaddingValues(
                horizontal = dimensionResource(RC.dimen.margin_tiny),
                vertical = dimensionResource(RC.dimen.margin_nano),
            ),
        ) {
            item {
                ComposeIconButton(
                    iconRes = R.drawable.icon_bold,
                    onClick = { },
                    iconButtonStyle = defaultComposeIconButtonStyle().copy(
                        containerColor = Color.Transparent,
                        outlineColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            }
        }
    }
}
