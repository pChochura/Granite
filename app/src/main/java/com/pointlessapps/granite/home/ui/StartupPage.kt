package com.pointlessapps.granite.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.pointlessapps.granite.R
import com.pointlessapps.granite.ui.components.ComposeButton
import com.pointlessapps.granite.ui.components.defaultComposeButtonStyle
import com.pointlessapps.granite.ui.components.defaultComposeTextStyle
import com.pointlessapps.granite.ui.R as RC

@Composable
internal fun StartupPage(
    viewModel: HomeViewModel,
    contentPadding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(
            space = dimensionResource(RC.dimen.margin_tiny),
            alignment = Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ComposeButton(
            label = stringResource(R.string.create_note),
            onClick = viewModel::onAddFileClicked,
            modifier = Modifier.padding(
                horizontal = dimensionResource(RC.dimen.margin_semi_big),
                vertical = dimensionResource(RC.dimen.margin_tiny),
            ),
            buttonStyle = defaultComposeButtonStyle().copy(
                iconRes = RC.drawable.ic_add_file,
                containerColor = MaterialTheme.colorScheme.primary,
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        )
        ComposeButton(
            label = stringResource(R.string.search),
            onClick = {},
            modifier = Modifier.padding(
                horizontal = dimensionResource(RC.dimen.margin_semi_big),
                vertical = dimensionResource(RC.dimen.margin_tiny),
            ),
            buttonStyle = defaultComposeButtonStyle().copy(
                iconRes = RC.drawable.ic_search,
                containerColor = MaterialTheme.colorScheme.primary,
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        )
    }
}
