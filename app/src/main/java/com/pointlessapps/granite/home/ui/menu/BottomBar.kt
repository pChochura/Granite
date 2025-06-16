package com.pointlessapps.granite.home.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import com.pointlessapps.granite.R
import com.pointlessapps.granite.ui.components.ComposeIconButton
import com.pointlessapps.granite.ui.components.defaultComposeIconButtonStyle
import com.pointlessapps.granite.ui.R as RC

@Composable
internal fun BottomBar(
    isFolded: Boolean,
    onAddFileClicked: () -> Unit,
    onAddFolderClicked: () -> Unit,
    onSortClicked: () -> Unit,
    onFoldClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.small.copy(
                    bottomStart = CornerSize(0),
                    bottomEnd = CornerSize(0),
                ),
            )
            .padding(start = dimensionResource(RC.dimen.margin_tiny))
            .padding(
                horizontal = dimensionResource(RC.dimen.margin_big),
                vertical = dimensionResource(RC.dimen.margin_medium),
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ComposeIconButton(
            iconRes = RC.drawable.ic_add_file,
            tooltipLabel = R.string.create_note,
            onClick = onAddFileClicked,
            iconButtonStyle = defaultComposeIconButtonStyle().copy(
                containerColor = Color.Transparent,
                outlineColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
        ComposeIconButton(
            iconRes = RC.drawable.ic_add_folder,
            tooltipLabel = R.string.create_folder,
            onClick = onAddFolderClicked,
            iconButtonStyle = defaultComposeIconButtonStyle().copy(
                containerColor = Color.Transparent,
                outlineColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
        ComposeIconButton(
            iconRes = RC.drawable.ic_sort,
            tooltipLabel = R.string.item_ordering,
            onClick = onSortClicked,
            iconButtonStyle = defaultComposeIconButtonStyle().copy(
                containerColor = Color.Transparent,
                outlineColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
        ComposeIconButton(
            iconRes = if (isFolded) RC.drawable.ic_unfold else RC.drawable.ic_fold,
            tooltipLabel = if (isFolded) R.string.unfold else R.string.fold,
            onClick = onFoldClicked,
            iconButtonStyle = defaultComposeIconButtonStyle().copy(
                containerColor = Color.Transparent,
                outlineColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
    }
}
