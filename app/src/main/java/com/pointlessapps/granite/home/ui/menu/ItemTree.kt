package com.pointlessapps.granite.home.ui.menu

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import com.pointlessapps.granite.R
import com.pointlessapps.granite.home.model.Item
import com.pointlessapps.granite.ui_components.components.ComposeIcon
import com.pointlessapps.granite.ui_components.components.ComposeText
import com.pointlessapps.granite.ui_components.components.defaultComposeTextStyle
import com.pointlessapps.granite.ui_components.R as RC

@Composable
internal fun ColumnScope.ItemTree(
    items: List<Item>,
    selectedItemId: Int?,
    openedFolderIds: Set<Int>,
    onItemSelected: (Item) -> Unit,
    onItemLongClick: (Item) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
    ) {
        items(items, key = { it.id }) { item ->
            Row(
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .then(
                        if (item.id == selectedItemId) {
                            Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                        } else {
                            Modifier
                        },
                    )
                    .combinedClickable(
                        role = Role.Button,
                        onClick = { onItemSelected(item) },
                        onLongClick = { onItemLongClick(item) },
                    )
                    .padding(
                        vertical = dimensionResource(RC.dimen.margin_tiny),
                        horizontal = dimensionResource(RC.dimen.margin_nano),
                    )
                    .padding(
                        start = dimensionResource(RC.dimen.margin_medium).times(item.indent),
                    ),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
            ) {
                if (item.isFolder) {
                    val rotation by animateFloatAsState(if (openedFolderIds.contains(item.id)) 90f else 0f)
                    ComposeIcon(
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.folder_icon_size))
                            .rotate(rotation),
                        iconRes = RC.drawable.ic_arrow_right,
                    )
                }

                ComposeText(
                    text = item.name,
                    textStyle = defaultComposeTextStyle().copy(
                        textColor = if (item.id == selectedItemId) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        typography = MaterialTheme.typography.bodySmall,
                    ),
                )
            }
        }
    }
}
