package com.pointlessapps.granite.login.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.granite.domain.auth.usecase.IsSignedInUseCase
import com.pointlessapps.granite.domain.auth.usecase.SignInAnonymouslyUseCase
import com.pointlessapps.granite.domain.auth.usecase.SignInUseCase
import com.pointlessapps.granite.navigation.Route
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow

internal sealed interface LoginEvent {
    data class NavigateTo(val route: Route) : LoginEvent
}

internal data class HomeState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = true,
)

internal class LoginViewModel(
    isSignedInUseCase: IsSignedInUseCase,
    private val signInUseCase: SignInUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
) : ViewModel() {

    var state by mutableStateOf(HomeState())
        private set

    private val eventChannel = Channel<LoginEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        isSignedInUseCase()
            .onStart { state = state.copy(isLoading = true) }
            .onEach {
                state = state.copy(isLoading = false)
                if (it) eventChannel.send(LoginEvent.NavigateTo(Route.Home))
            }
            .catch {
                state = state.copy(isLoading = true)
                /* handle errors */
            }
            .launchIn(viewModelScope)
    }

    fun login() {
        signInAnonymouslyUseCase()
            .onStart { state = state.copy(isLoading = true) }
            .onEach {
                state = state.copy(isLoading = false)
                eventChannel.send(LoginEvent.NavigateTo(Route.Home))
            }
            .catch {
                state = state.copy(isLoading = true)
                /* handle errors */
            }
            .launchIn(viewModelScope)
    }

    fun onPasswordChanged(password: String) {
        state = state.copy(password = password)
    }

    fun onEmailChanged(email: String) {
        state = state.copy(email = email)
    }
}
