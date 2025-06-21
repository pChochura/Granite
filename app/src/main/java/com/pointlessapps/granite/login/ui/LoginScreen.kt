package com.pointlessapps.granite.login.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.pointlessapps.granite.LocalSnackbarHostState
import com.pointlessapps.granite.navigation.Route
import com.pointlessapps.granite.ui.R
import com.pointlessapps.granite.ui.components.ComposeButton
import com.pointlessapps.granite.ui.components.ComposeLoader
import com.pointlessapps.granite.ui.components.ComposeScaffoldLayout
import com.pointlessapps.granite.ui.components.ComposeTextField
import com.pointlessapps.granite.ui.components.defaultComposeTextFieldStyle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    onNavigateTo: (Route) -> Unit,
) {
    val localSnackbarHostState = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()

    LifecycleResumeEffect(Unit) {
        coroutineScope.launch {
            viewModel.events.collect {
                when (it) {
                    is LoginEvent.NavigateTo -> onNavigateTo(it.route)
                    is LoginEvent.ShowSnackbar -> localSnackbarHostState.showSnackbar(it.message)
                }
            }
        }

        onPauseOrDispose { }
    }

    ComposeScaffoldLayout(
        modifier = Modifier.systemBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(all = dimensionResource(R.dimen.margin_medium)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.margin_nano),
                Alignment.CenterVertically,
            ),
        ) {
            ComposeTextField(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(dimensionResource(R.dimen.margin_tiny)),
                value = viewModel.state.email,
                onValueChange = viewModel::onEmailChanged,
                textFieldStyle = defaultComposeTextFieldStyle().copy(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                    ),
                ),
            )
            ComposeTextField(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(dimensionResource(R.dimen.margin_tiny)),
                value = viewModel.state.password,
                onValueChange = viewModel::onPasswordChanged,
                textFieldStyle = defaultComposeTextFieldStyle().copy(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                    ),
                ),
            )
            ComposeButton("Login", onClick = viewModel::login)
            ComposeButton("Login with google", onClick = viewModel::loginWithGoogle)
        }

        ComposeLoader(viewModel.state.isLoading, scrimAlpha = 1f)
    }
}
