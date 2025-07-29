package com.pointlessapps.granite.home.ui.components.menu

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import com.pointlessapps.granite.R
import com.pointlessapps.granite.fuzzy.search.FuzzySearch
import com.pointlessapps.granite.model.Item
import com.pointlessapps.granite.ui.components.ComposeIcon
import com.pointlessapps.granite.ui.components.ComposeText
import com.pointlessapps.granite.ui.components.defaultComposeIconStyle
import com.pointlessapps.granite.ui.components.defaultComposeTextStyle
import kotlinx.coroutines.delay
import com.pointlessapps.granite.ui.R as RC

@Composable
internal fun ColumnScope.ItemTree(
    highlightedItem: Item?,
    items: List<Item>,
    deletedItems: List<Item>,
    searchValue: String,
    selectedItemId: Int?,
    openedFolderIds: Set<Int>,
    onItemSelected: (Item) -> Unit,
    onItemLongClick: (Item) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var isTrashOpened by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(highlightedItem) {
        highlightedItem?.let { item ->
            // Account for the divider between sections
            val index = if (item.deleted) deletedItems.indexOf(item) + 1 else items.indexOf(item)
            if (item.deleted) {
                isTrashOpened = true
            }

            // Wait for the current animations to finish
            delay(500)
            listState.animateScrollToItem(index)
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            keyboardController?.hide()
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
        contentPadding = PaddingValues(
            top = dimensionResource(RC.dimen.margin_medium),
            bottom = dimensionResource(RC.dimen.margin_huge),
        ),
    ) {
        items(items, key = { it.id }) { item ->
            Item(
                item = item,
                searchValue = searchValue,
                isHighlighted = item == highlightedItem,
                isFileOpened = selectedItemId == item.id,
                isFolderOpened = openedFolderIds.contains(item.id),
                onItemSelected = onItemSelected,
                onItemLongClick = onItemLongClick,
            )
        }

        if (deletedItems.isNotEmpty()) {
            item(key = "Deleted") {
                Column(Modifier.animateItem()) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .clickable(
                                role = Role.Button,
                                onClick = { isTrashOpened = !isTrashOpened },
                            )
                            .padding(
                                vertical = dimensionResource(RC.dimen.margin_tiny),
                                horizontal = dimensionResource(RC.dimen.margin_nano),
                            ),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val rotation by animateFloatAsState(
                            if (isTrashOpened && searchValue.isBlank()) 90f else 0f,
                        )
                        ComposeIcon(
                            modifier = Modifier
                                .size(dimensionResource(R.dimen.folder_icon_size))
                                .rotate(rotation),
                            iconRes = RC.drawable.ic_arrow_right,
                            iconStyle = defaultComposeIconStyle().copy(
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                        ComposeText(
                            text = stringResource(R.string.deleted),
                            textStyle = defaultComposeTextStyle().copy(
                                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                typography = MaterialTheme.typography.titleMedium,
                            ),
                        )
                    }
                }
            }
        }

        if (isTrashOpened || searchValue.isNotBlank()) {
            items(deletedItems, key = { it.id }) { item ->
                Item(
                    item = item,
                    searchValue = searchValue,
                    isHighlighted = item == highlightedItem,
                    isFileOpened = selectedItemId == item.id,
                    isFolderOpened = openedFolderIds.contains(item.id),
                    onItemSelected = onItemSelected,
                    onItemLongClick = onItemLongClick,
                )
            }
        }
    }
}

@Composable
private fun LazyItemScope.Item(
    item: Item,
    searchValue: String,
    isHighlighted: Boolean,
    isFileOpened: Boolean,
    isFolderOpened: Boolean,
    onItemSelected: (Item) -> Unit,
    onItemLongClick: (Item) -> Unit,
) {
    val borderColor by animateColorAsState(
        targetValue = if (isHighlighted) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = repeatable(1, tween(500), RepeatMode.Reverse),
    )

    val searchMatches = remember(searchValue) {
        FuzzySearch.extractWords(searchValue, item.name)
    }

    Row(
        modifier = Modifier
            .animateItem()
            .fillMaxWidth()
            .alpha(if (item.deleted) 0.5f else 1f)
            .clip(MaterialTheme.shapes.small)
            .border(
                width = dimensionResource(RC.dimen.default_border_width),
                color = borderColor,
                shape = MaterialTheme.shapes.small,
            )
            .then(
                if (isFileOpened) {
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
            .then(
                if (searchValue.isBlank()) {
                    Modifier.padding(start = dimensionResource(RC.dimen.margin_medium).times(item.indent))
                } else {
                    Modifier
                }
            ),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (item.isFolder) {
            val rotation by animateFloatAsState(
                if (isFolderOpened && searchValue.isBlank()) 90f else 0f,
            )
            ComposeIcon(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.folder_icon_size))
                    .rotate(rotation),
                iconRes = RC.drawable.ic_arrow_right,
                iconStyle = defaultComposeIconStyle().copy(
                    tint = if (isFileOpened) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                ),
            )
        }

        ComposeText(
            text = buildAnnotatedString {
                append(item.name)
                if (searchValue.isNotBlank()) {
                    searchMatches?.matches?.forEach {
                        addStyle(
                            style = SpanStyle(fontWeight = FontWeight.Bold),
                            start = it.first,
                            end = it.last,
                        )
                    }
                }
            },
            textStyle = defaultComposeTextStyle().copy(
                textColor = if (isFileOpened) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                typography = MaterialTheme.typography.bodySmall,
            ),
        )
    }
}
