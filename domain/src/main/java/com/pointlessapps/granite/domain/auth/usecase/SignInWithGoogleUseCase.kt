package com.pointlessapps.granite.domain.auth.usecase

import com.pointlessapps.granite.domain.auth.AuthRepository

class SignInWithGoogleUseCase(
    private val authRepository: AuthRepository,
) {
    operator fun invoke() = authRepository.signInWithGoogle()
}
