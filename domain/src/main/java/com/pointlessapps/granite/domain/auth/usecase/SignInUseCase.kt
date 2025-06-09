package com.pointlessapps.granite.domain.auth.usecase

import com.pointlessapps.granite.domain.auth.AuthRepository

class SignInUseCase(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(email: String, password: String) = authRepository.signIn(email, password)
}
