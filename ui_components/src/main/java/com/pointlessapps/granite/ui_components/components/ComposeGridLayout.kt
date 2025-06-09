package com.pointlessapps.granite.ui_components.components

import androidx.annotation.IntRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize

@Composable
fun ComposeGridLayout(
    @IntRange(from = 0) rows: Int,
    @IntRange(from = 0) columns: Int,
    modifier: Modifier = Modifier,
    item: @Composable DpSize.(Int) -> Unit,
) {
    SubcomposeLayout(modifier) { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight
        val itemWidth = layoutWidth / columns
        val itemHeight = layoutHeight / rows

        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        val bodyContentPlaceables = subcompose(Unit) {
            repeat(rows * columns) { index ->
                item(
                    with(LocalDensity.current) { DpSize(itemWidth.toDp(), itemHeight.toDp()) },
                    index,
                )
            }
        }.map { it.measure(looseConstraints) }

        layout(layoutWidth, layoutHeight) {
            bodyContentPlaceables.forEachIndexed { index, placeable ->
                val y = index / rows
                val x = index - y * rows
                placeable.place(x * itemWidth, y * itemHeight)
            }
        }
    }
}
