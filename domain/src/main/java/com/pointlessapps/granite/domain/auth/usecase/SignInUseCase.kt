package com.pointlessapps.granite.domain.auth.usecase

import com.pointlessapps.granite.supabase.datasource.auth.SupabaseAuthDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SignInUseCase(
    private val authDatasource: SupabaseAuthDatasource,
) {
    suspend operator fun invoke(email: String, password: String) = withContext(Dispatchers.IO) {
        authDatasource.signIn(email, password)
    }
}
