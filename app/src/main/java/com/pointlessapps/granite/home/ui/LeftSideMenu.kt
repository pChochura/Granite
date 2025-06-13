package com.pointlessapps.granite.home.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.pointlessapps.granite.R
import com.pointlessapps.granite.home.model.Item
import com.pointlessapps.granite.home.model.ItemOrderType
import com.pointlessapps.granite.ui_components.components.ComposeButton
import com.pointlessapps.granite.ui_components.components.ComposeDialog
import com.pointlessapps.granite.ui_components.components.ComposeDialogDismissible
import com.pointlessapps.granite.ui_components.components.ComposeIcon
import com.pointlessapps.granite.ui_components.components.ComposeIconButton
import com.pointlessapps.granite.ui_components.components.ComposeText
import com.pointlessapps.granite.ui_components.components.ComposeTextField
import com.pointlessapps.granite.ui_components.components.defaultComposeButtonStyle
import com.pointlessapps.granite.ui_components.components.defaultComposeButtonTextStyle
import com.pointlessapps.granite.ui_components.components.defaultComposeDialogStyle
import com.pointlessapps.granite.ui_components.components.defaultComposeIconButtonStyle
import com.pointlessapps.granite.ui_components.components.defaultComposeTextFieldStyle
import com.pointlessapps.granite.ui_components.components.defaultComposeTextStyle
import com.pointlessapps.granite.ui_components.R as RC

private data class CreateFolderDialogData(
    val name: TextFieldValue,
    val parentId: Int?,
)

@Composable
internal fun LeftSideMenu(
    viewModel: HomeViewModel,
) {
    var searchValue by remember { mutableStateOf("") }
    var createFolderDialogData by remember { mutableStateOf<CreateFolderDialogData?>(null) }
    var showOrderTypeDialog by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = dimensionResource(RC.dimen.margin_tiny))
                .padding(all = dimensionResource(RC.dimen.margin_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_medium)),
        ) {
            Title()
            SearchBar(
                searchValue = searchValue,
                onSearchValueChanged = { searchValue = it },
            )
            ItemTree(
                items = viewModel.state.filteredItems,
                selectedItemId = viewModel.state.openedItemId,
                openedFolderIds = viewModel.state.openedFolderIds,
                onItemSelected = viewModel::onItemSelected,
            )
        }

        val untitledText = stringResource(R.string.untitled)
        BottomBar(
            isFolded = viewModel.state.openedFolderIds.isEmpty(),
            onAddFileClicked = viewModel::onAddFileClicked,
            onAddFolderClicked = {
                createFolderDialogData = CreateFolderDialogData(
                    name = TextFieldValue(
                        text = untitledText,
                        selection = TextRange(0, untitledText.length),
                    ),
                    parentId = null,
                )
                keyboardController?.show()
            },
            onSortClicked = { showOrderTypeDialog = true },
            onFoldClicked = viewModel::onFoldClicked,
        )
    }

    if (showOrderTypeDialog) {
        OrderTypeDialog(
            onOrderTypeSelected = viewModel::onOrderTypeSelected,
            onDismissRequest = { showOrderTypeDialog = false },
        )
    }

    createFolderDialogData?.let { data ->
        CreateFolderDialog(
            data = data,
            onDataChanged = { createFolderDialogData = it },
            onFolderCreated = {
                viewModel.createFolder(data.name.text, data.parentId)
                createFolderDialogData = null
            },
            onDismissRequest = { createFolderDialogData = null },
        )
    }
}

@Composable
private fun OrderTypeDialog(
    onOrderTypeSelected: (ItemOrderType) -> Unit,
    onDismissRequest: () -> Unit,
) {
    ComposeDialog(
        onDismissRequest = onDismissRequest,
        dialogStyle = defaultComposeDialogStyle().copy(
            label = stringResource(R.string.item_ordering),
            iconRes = RC.drawable.ic_sort,
            dismissible = ComposeDialogDismissible.Both,
        ),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ItemOrderType.entries.forEach { entry ->
                ComposeText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        .clickable { onOrderTypeSelected(entry) }
                        .padding(
                            vertical = dimensionResource(RC.dimen.margin_tiny),
                            horizontal = dimensionResource(RC.dimen.margin_medium),
                        ),
                    text = stringResource(entry.label),
                    textStyle = defaultComposeTextStyle().copy(
                        textColor = MaterialTheme.colorScheme.onSurface,
                        typography = MaterialTheme.typography.labelLarge,
                    ),
                )
            }
        }
    }
}

@Composable
private fun CreateFolderDialog(
    data: CreateFolderDialogData,
    onDataChanged: (CreateFolderDialogData) -> Unit,
    onFolderCreated: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val newFolderFocusRequester = remember { FocusRequester() }
    LaunchedEffect(newFolderFocusRequester) {
        newFolderFocusRequester.requestFocus()
    }

    ComposeDialog(
        onDismissRequest = onDismissRequest,
        dialogStyle = defaultComposeDialogStyle().copy(
            label = stringResource(R.string.create_folder),
            iconRes = RC.drawable.ic_add_folder,
            dismissible = ComposeDialogDismissible.OnBackPress,
        ),
    ) {
        ComposeTextField(
            value = data.name,
            onValueChange = { onDataChanged(data.copy(name = it)) },
            modifier = Modifier
                .focusRequester(newFolderFocusRequester)
                .fillMaxWidth()
                .border(
                    width = dimensionResource(RC.dimen.default_border_width),
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.small,
                )
                .padding(dimensionResource(RC.dimen.margin_medium)),
            onImeAction = {
                if (it == ImeAction.Done) {
                    onFolderCreated()
                }
            },
            textFieldStyle = defaultComposeTextFieldStyle().copy(
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                    showKeyboardOnFocus = true,
                ),
            ),
        )

        AnimatedContent(data.name.text.isNotBlank()) { enabled ->
            ComposeButton(
                label = stringResource(R.string.save),
                onClick = onFolderCreated,
                buttonStyle = defaultComposeButtonStyle().copy(
                    iconRes = RC.drawable.ic_done,
                    enabled = enabled,
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(0.1f),
                    textStyle = defaultComposeButtonTextStyle().copy(
                        textColor = if (enabled) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(0.38f)
                        },
                    ),
                ),
            )
        }
    }
}

@Composable
private fun Title() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_tiny)),
    ) {
        Image(
            modifier = Modifier
                .size(dimensionResource(R.dimen.logo_icon_size))
                .background(colorResource(R.color.ic_launcher_background), shape = CircleShape)
                .padding(dimensionResource(RC.dimen.margin_tiny)),
            painter = painterResource(R.drawable.ic_logo),
            contentDescription = null,
        )
        ComposeText(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.app_name),
            textStyle = defaultComposeTextStyle().copy(
                textColor = MaterialTheme.colorScheme.onSurface,
                typography = MaterialTheme.typography.titleLarge,
            ),
        )

        ComposeIconButton(
            iconRes = RC.drawable.ic_settings,
            onClick = {},
            iconButtonStyle = defaultComposeIconButtonStyle().copy(
                containerColor = Color.Transparent,
                outlineColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
    }
}

@Composable
private fun SearchBar(searchValue: String, onSearchValueChanged: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(
                start = dimensionResource(RC.dimen.margin_medium),
                end = dimensionResource(RC.dimen.margin_tiny),
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
    ) {
        ComposeIcon(
            modifier = Modifier.padding(vertical = dimensionResource(RC.dimen.margin_small)),
            iconRes = RC.drawable.ic_search,
        )
        ComposeTextField(
            modifier = Modifier.weight(1f),
            value = searchValue,
            onValueChange = onSearchValueChanged,
            textFieldStyle = defaultComposeTextFieldStyle().copy(
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search,
                    showKeyboardOnFocus = true,
                ),
                placeholder = stringResource(R.string.search),
                maxLines = 1,
            )
        )
        AnimatedVisibility(searchValue.isNotEmpty()) {
            ComposeIconButton(
                iconRes = RC.drawable.ic_close,
                onClick = { onSearchValueChanged("") },
                iconButtonStyle = defaultComposeIconButtonStyle().copy(
                    containerColor = Color.Transparent,
                    outlineColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        }
    }
}

@Composable
private fun ColumnScope.ItemTree(
    items: List<Item>,
    selectedItemId: Int?,
    openedFolderIds: Set<Int>,
    onItemSelected: (Item) -> Unit,
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
                    .clickable(onClick = { onItemSelected(item) })
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

@Composable
private fun BottomBar(
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
            onClick = onAddFileClicked,
            iconButtonStyle = defaultComposeIconButtonStyle().copy(
                containerColor = Color.Transparent,
                outlineColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
        ComposeIconButton(
            iconRes = RC.drawable.ic_add_folder,
            onClick = onAddFolderClicked,
            iconButtonStyle = defaultComposeIconButtonStyle().copy(
                containerColor = Color.Transparent,
                outlineColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
        ComposeIconButton(
            iconRes = RC.drawable.ic_sort,
            onClick = onSortClicked,
            iconButtonStyle = defaultComposeIconButtonStyle().copy(
                containerColor = Color.Transparent,
                outlineColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
        ComposeIconButton(
            iconRes = if (isFolded) RC.drawable.ic_unfold else RC.drawable.ic_fold,
            onClick = onFoldClicked,
            iconButtonStyle = defaultComposeIconButtonStyle().copy(
                containerColor = Color.Transparent,
                outlineColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
    }
}
