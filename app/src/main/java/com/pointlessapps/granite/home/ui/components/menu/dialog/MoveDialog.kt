package com.pointlessapps.granite.home.ui.components.menu.dialog

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.granite.R
import com.pointlessapps.granite.fuzzy.search.FuzzySearch
import com.pointlessapps.granite.fuzzy.search.SearchMatch
import com.pointlessapps.granite.home.model.ItemWithParents
import com.pointlessapps.granite.ui.components.ComposeButton
import com.pointlessapps.granite.ui.components.ComposeDialog
import com.pointlessapps.granite.ui.components.ComposeDialogDismissible
import com.pointlessapps.granite.ui.components.ComposeText
import com.pointlessapps.granite.ui.components.ComposeTextField
import com.pointlessapps.granite.ui.components.defaultComposeButtonStyle
import com.pointlessapps.granite.ui.components.defaultComposeButtonTextStyle
import com.pointlessapps.granite.ui.components.defaultComposeDialogStyle
import com.pointlessapps.granite.ui.components.defaultComposeTextFieldStyle
import com.pointlessapps.granite.ui.components.defaultComposeTextStyle
import com.pointlessapps.granite.ui.R as RC

@Composable
internal fun MoveDialog(
    data: MoveDialogData,
    onInputChanged: (String) -> Unit,
    onItemClicked: (ItemWithParents) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var selectedItem by remember { mutableStateOf<ItemWithParents?>(null) }

    ComposeDialog(
        onDismissRequest = onDismissRequest,
        dialogStyle = defaultComposeDialogStyle().copy(
            label = stringResource(R.string.move_to),
            iconRes = RC.drawable.ic_move,
            dismissible = if (selectedItem != null) {
                ComposeDialogDismissible.None
            } else {
                ComposeDialogDismissible.OnBackPress
            },
        ),
    ) {
        BackHandler(selectedItem != null) {
            selectedItem = null
        }

        AnimatedContent(selectedItem) { item ->
            if (item != null) {
                Confirmation(
                    itemWithParents = item,
                    onConfirmClicked = {
                        onItemClicked(item)
                        onDismissRequest()
                    },
                    onCancelClicked = { selectedItem = null },
                )
            } else {
                ItemTree(
                    data = data,
                    onItemClicked = { selectedItem = it },
                    onInputChanged = onInputChanged,
                )
            }
        }
    }
}

@Composable
private fun Confirmation(
    itemWithParents: ItemWithParents,
    onConfirmClicked: () -> Unit,
    onCancelClicked: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = dimensionResource(RC.dimen.margin_big),
            alignment = Alignment.CenterVertically,
        ),
    ) {
        ComposeText(
            text = stringResource(R.string.move_item_confirmation),
            textStyle = defaultComposeTextStyle().copy(
                typography = MaterialTheme.typography.bodyMedium,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            ),
        )

        ComposeText(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = MaterialTheme.shapes.small
                )
                .border(
                    width = dimensionResource(RC.dimen.default_border_width),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = MaterialTheme.shapes.small,
                )
                .padding(
                    horizontal = dimensionResource(RC.dimen.margin_medium),
                    vertical = dimensionResource(RC.dimen.margin_small),
                ),
            text = itemWithParents.toString(),
            textStyle = defaultComposeTextStyle().copy(
                typography = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                textColor = MaterialTheme.colorScheme.onSurface,
            ),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_medium)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ComposeButton(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.move),
                onClick = onConfirmClicked,
                buttonStyle = defaultComposeButtonStyle().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    textStyle = defaultComposeButtonTextStyle().copy(
                        textAlign = TextAlign.Center,
                        textColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ),
            )
            ComposeButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = dimensionResource(RC.dimen.default_border_width),
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape,
                    ),
                label = stringResource(R.string.cancel),
                onClick = onCancelClicked,
                buttonStyle = defaultComposeButtonStyle().copy(
                    containerColor = Color.Transparent,
                    textStyle = defaultComposeButtonTextStyle().copy(
                        textAlign = TextAlign.Center,
                        textColor = MaterialTheme.colorScheme.onSurface,
                    ),
                ),
            )
        }
    }
}

@Composable
private fun ItemTree(
    data: MoveDialogData,
    onItemClicked: (ItemWithParents) -> Unit,
    onInputChanged: (String) -> Unit,
) {
    val nameFocusRequester = remember { FocusRequester() }
    LaunchedEffect(nameFocusRequester) {
        nameFocusRequester.requestFocus()
    }

    Column {
        LazyColumn(
            modifier = Modifier
                .height(dimensionResource(R.dimen.dialog_content_height))
                .fillMaxWidth()
                .border(
                    width = dimensionResource(RC.dimen.default_border_width),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = MaterialTheme.shapes.small.copy(
                        bottomEnd = CornerSize(0),
                        bottomStart = CornerSize(0),
                    ),
                ),
            verticalArrangement = Arrangement.spacedBy(
                space = dimensionResource(RC.dimen.margin_nano),
                alignment = Alignment.Bottom,
            ),
            reverseLayout = true,
        ) {
            items(data.filteredFolders, key = { it.item.id ?: 0 }) {
                Item(
                    match = it,
                    onItemSelected = onItemClicked,
                )
            }
        }

        ComposeTextField(
            value = data.query,
            onValueChange = { onInputChanged(it) },
            modifier = Modifier
                .focusRequester(nameFocusRequester)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = MaterialTheme.shapes.small.copy(
                        topStart = CornerSize(0),
                        topEnd = CornerSize(0),
                    ),
                )
                .border(
                    width = dimensionResource(RC.dimen.default_border_width),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = MaterialTheme.shapes.small.copy(
                        topStart = CornerSize(0),
                        topEnd = CornerSize(0),
                    ),
                )
                .padding(dimensionResource(RC.dimen.margin_medium)),
            textFieldStyle = defaultComposeTextFieldStyle().copy(
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    showKeyboardOnFocus = true,
                ),
                placeholder = stringResource(R.string.search),
                textColor = MaterialTheme.colorScheme.onSurface,
                placeholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            ),
        )
    }
}

@Composable
private fun LazyItemScope.Item(
    match: SearchMatch<ItemWithParents>,
    onItemSelected: (ItemWithParents) -> Unit,
) {
    ComposeText(
        modifier = Modifier
            .animateItem()
            .fillMaxWidth()
            .border(
                width = dimensionResource(RC.dimen.default_border_width),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            .clickable(
                role = Role.Button,
                onClick = { onItemSelected(match.item) },
            )
            .padding(
                vertical = dimensionResource(RC.dimen.margin_tiny),
                horizontal = dimensionResource(RC.dimen.margin_medium),
            ),
        text = buildAnnotatedString {
            append(match.item.toString())
            match.matches.forEach { range ->
                addStyle(
                    style = SpanStyle(fontWeight = FontWeight.Bold),
                    start = range.first,
                    end = range.last,
                )
            }
        },
        textStyle = defaultComposeTextStyle().copy(
            textColor = MaterialTheme.colorScheme.onSurface,
            typography = MaterialTheme.typography.labelMedium,
        ),
    )
}

internal data class MoveDialogData(
    val itemId: Int,
    val folders: List<ItemWithParents>,
    val query: String = "",
) {
    val filteredFolders: List<SearchMatch<ItemWithParents>>
        get() = if (query.isBlank()) {
            folders.map { SearchMatch(it, emptyList()) }
        } else {
            FuzzySearch.extractPathsSorted(query = query, paths = folders)
        }
}
