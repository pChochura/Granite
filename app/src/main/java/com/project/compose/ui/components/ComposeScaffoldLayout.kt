package com.project.compose.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout

@Composable
internal fun ComposeScaffoldLayout(
    topBar: @Composable () -> Unit = {},
    fab: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    SubcomposeLayout(modifier = Modifier.imePadding()) { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight

        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        layout(layoutWidth, layoutHeight) {
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
                content(
                    PaddingValues(
                        top = topBarHeight.toDp(),
                        bottom = fabHeight.toDp(),
                    ),
                )
            }.map { it.measure(looseConstraints.copy(maxHeight = layoutHeight)) }

            bodyContentPlaceables.forEach { it.place(0, 0) }
            topBarPlaceables.forEach { it.place(0, 0) }
            fabPlaceables.forEach { fab ->
                fab.place(0, layoutHeight - fabHeight)
            }
        }
    }
}

private enum class ComposeScaffoldLayoutContent { TopBar, Content, Fab }
