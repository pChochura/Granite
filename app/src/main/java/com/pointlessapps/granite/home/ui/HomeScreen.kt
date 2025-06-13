package com.pointlessapps.granite.home.ui

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.pointlessapps.granite.R
import com.pointlessapps.granite.home.ui.menu.LeftSideMenu
import com.pointlessapps.granite.navigation.Route
import com.pointlessapps.granite.ui_components.components.ComposeIconButton
import com.pointlessapps.granite.ui_components.components.ComposeLoader
import com.pointlessapps.granite.ui_components.components.ComposeScaffoldLayout
import com.pointlessapps.granite.ui_components.components.ComposeText
import com.pointlessapps.granite.ui_components.components.defaultComposeIconButtonStyle
import com.pointlessapps.granite.ui_components.components.defaultComposeTextStyle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt
import com.pointlessapps.granite.ui_components.R as RC

@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateTo: (Route) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    LifecycleResumeEffect(Unit) {
        coroutineScope.launch {
            viewModel.events.collect {
                when (it) {
                    is HomeEvent.CloseDrawer -> drawerState.close()
                }
            }
        }

        onPauseOrDispose {
            viewModel.saveNote()
        }
    }

    LaunchedEffect(drawerState.isOpen) {
        focusManager.clearFocus()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerState) {
                LeftSideMenu(viewModel)
            }
        },
        content = {
            ComposeScaffoldLayout(
                modifier = Modifier.offset {
                    IntOffset(
                        x = DrawerDefaults.MaximumDrawerWidth.roundToPx() + when {
                            !drawerState.currentOffset.isNaN() -> drawerState.currentOffset.roundToInt()
                            else -> 0
                        },
                        y = 0,
                    )
                },
                topBar = { TopBar(onMenuClicked = { coroutineScope.launch { drawerState.open() } }) },
                content = { contentPadding ->
                    AnimatedContent(viewModel.state.openedItemId == null) { emptyScreen ->
                        if (emptyScreen) {
                            StartupPage(
                                viewModel = viewModel,
                                contentPadding = contentPadding,
                            )
                        } else {
                            Editor(
                                viewModel = viewModel,
                                contentPadding = contentPadding,
                            )
                        }
                    }
                },
            )

            ComposeLoader(viewModel.state.isLoading)
        }
    )
}

@Composable
private fun TopBar(onMenuClicked: () -> Unit) {
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
            iconRes = RC.drawable.ic_move_handle,
            tooltipLabel = R.string.menu,
            onClick = onMenuClicked,
            iconButtonStyle = defaultComposeIconButtonStyle().copy(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                outlineColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
        ComposeText(
            text = stringResource(R.string.note),
            modifier = Modifier.weight(1f),
            textStyle = defaultComposeTextStyle().copy(
                textColor = MaterialTheme.colorScheme.onSurface,
                typography = MaterialTheme.typography.titleLarge,
            ),
        )

        ComposeIconButton(
            iconRes = RC.drawable.ic_warning,
            tooltipLabel = R.string.file_info,
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
                    tooltipLabel = R.string.bold,
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
