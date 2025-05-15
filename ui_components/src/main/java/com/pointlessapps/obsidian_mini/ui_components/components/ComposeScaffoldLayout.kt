package com.pointlessapps.obsidian_mini.ui_components.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity

@Composable
fun ComposeScaffoldLayout(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    fab: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    SubcomposeLayout(modifier) { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight

        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        val topBarPlaceables = subcompose(ComposeScaffoldLayoutContent.TopBar, topBar).map {
            it.measure(looseConstraints)
        }

        val topBarHeight = topBarPlaceables.maxByOrNull { it.height }?.height ?: 0

        val fabPlaceables = subcompose(ComposeScaffoldLayoutContent.Fab, fab)
            .mapNotNull { measurable ->
                measurable.measure(looseConstraints).takeIf { it.height != 0 && it.width != 0 }
            }

        val fabHeight = fabPlaceables.maxByOrNull { it.height }?.height ?: 0

        val bodyContentPlaceables = subcompose(ComposeScaffoldLayoutContent.Content) {
            val bottomPadding by animateDpAsState(
                maxOf(
                    fabHeight,
                    WindowInsets.ime.getBottom(LocalDensity.current),
                    WindowInsets.navigationBars.getBottom(LocalDensity.current),
                ).toDp(),
                label = "Bottom padding",
            )

            content(
                PaddingValues(
                    top = topBarHeight.toDp(),
                    bottom = bottomPadding,
                ),
            )
        }.map { it.measure(looseConstraints.copy(maxHeight = layoutHeight)) }

        layout(layoutWidth, layoutHeight) {
            bodyContentPlaceables.forEach { it.place(0, 0) }
            topBarPlaceables.forEach { it.place(0, 0) }
            fabPlaceables.forEach { fab ->
                fab.place(0, layoutHeight - fabHeight)
            }
        }
    }
}

private enum class ComposeScaffoldLayoutContent { TopBar, Content, Fab }
