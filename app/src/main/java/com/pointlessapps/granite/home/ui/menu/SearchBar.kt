package com.pointlessapps.granite.home.ui.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.pointlessapps.granite.R
import com.pointlessapps.granite.ui.components.ComposeIcon
import com.pointlessapps.granite.ui.components.ComposeIconButton
import com.pointlessapps.granite.ui.components.ComposeTextField
import com.pointlessapps.granite.ui.components.defaultComposeIconButtonStyle
import com.pointlessapps.granite.ui.components.defaultComposeTextFieldStyle
import com.pointlessapps.granite.ui.R as RC

@Composable
internal fun SearchBar(searchValue: String, onSearchValueChanged: (String) -> Unit) {
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
                tooltipLabel = R.string.clear,
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
