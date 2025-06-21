package com.pointlessapps.granite.home.ui.components.menu.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.pointlessapps.granite.R
import com.pointlessapps.granite.fuzzy.search.PathMatch
import com.pointlessapps.granite.fuzzy.search.PathSearch
import com.pointlessapps.granite.home.model.ItemWithParents
import com.pointlessapps.granite.ui.components.ComposeDialog
import com.pointlessapps.granite.ui.components.ComposeDialogDismissible
import com.pointlessapps.granite.ui.components.ComposeText
import com.pointlessapps.granite.ui.components.ComposeTextField
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
    val nameFocusRequester = remember { FocusRequester() }
    LaunchedEffect(nameFocusRequester) {
        nameFocusRequester.requestFocus()
    }

    ComposeDialog(
        modifier = Modifier.height(dimensionResource(R.dimen.dialog_height)),
        onDismissRequest = onDismissRequest,
        dialogStyle = defaultComposeDialogStyle().copy(
            label = stringResource(R.string.move_to),
            iconRes = null,
            dismissible = ComposeDialogDismissible.OnBackPress,
        ),
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
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
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(dimensionResource(RC.dimen.margin_medium)),
            textFieldStyle = defaultComposeTextFieldStyle().copy(
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    showKeyboardOnFocus = true,
                ),
                placeholder = stringResource(R.string.search),
            ),
        )
    }
}

@Composable
private fun LazyItemScope.Item(
    match: PathMatch<ItemWithParents>,
    onItemSelected: (ItemWithParents) -> Unit,
) {
    Row(
        modifier = Modifier
            .animateItem()
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(
                role = Role.Button,
                onClick = { onItemSelected(match.item) },
            )
            .padding(
                vertical = dimensionResource(RC.dimen.margin_tiny),
                horizontal = dimensionResource(RC.dimen.margin_nano),
            ),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ComposeText(
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
                typography = MaterialTheme.typography.bodySmall,
            ),
        )
    }
}

internal data class MoveDialogData(
    val itemId: Int,
    val folders: List<ItemWithParents>,
    val query: String = "",
) {
    val filteredFolders: List<PathMatch<ItemWithParents>>
        get() = if (query.isBlank()) {
            folders.map { PathMatch(it, emptyList()) }
        } else {
            PathSearch.extractSorted(query = query, paths = folders)
        }
}
