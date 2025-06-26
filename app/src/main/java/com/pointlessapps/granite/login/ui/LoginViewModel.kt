package com.pointlessapps.granite.login.ui

import android.app.Application
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.pointlessapps.granite.R
import com.pointlessapps.granite.domain.auth.usecase.SignInAnonymouslyUseCase
import com.pointlessapps.granite.domain.auth.usecase.SignInUseCase
import com.pointlessapps.granite.domain.auth.usecase.SignInWithGoogleUseCase
import com.pointlessapps.granite.navigation.Route
import com.pointlessapps.granite.utils.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

internal sealed interface LoginEvent {
    data class NavigateTo(val route: Route) : LoginEvent
    data class ShowSnackbar(@StringRes val message: Int) : LoginEvent
}

internal data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
)

internal class LoginViewModel(
    application: Application,
    private val signInUseCase: SignInUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
) : AndroidViewModel(application) {

    var state by mutableStateOf(LoginState())
        private set

    private val eventChannel = Channel<LoginEvent>()
    val events = eventChannel.receiveAsFlow()

    fun login() {
        launch(
            onException = {
                it.printStackTrace()
                state = state.copy(isLoading = false)
                eventChannel.trySend(LoginEvent.ShowSnackbar(R.string.error_signing_in))
            },
        ) {
            state = state.copy(isLoading = true)

            signInUseCase(state.email, state.password)
            eventChannel.send(LoginEvent.NavigateTo(Route.Home))

            state = state.copy(isLoading = false)
        }
    }

    fun loginWithGoogle() {
        launch(
            onException = {
                it.printStackTrace()
                state = state.copy(isLoading = false)
                eventChannel.trySend(LoginEvent.ShowSnackbar(R.string.error_signing_in))
            },
        ) {
            state = state.copy(isLoading = true)

            signInWithGoogleUseCase(getApplication())
            eventChannel.send(LoginEvent.NavigateTo(Route.Home))

            state = state.copy(isLoading = false)
        }
    }

    fun onPasswordChanged(password: String) {
        state = state.copy(password = password)
    }

    fun onEmailChanged(email: String) {
        state = state.copy(email = email)
    }
}
