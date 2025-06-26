package com.pointlessapps.granite.domain.auth.usecase

import com.pointlessapps.granite.supabase.datasource.auth.SupabaseAuthDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SignInAnonymouslyUseCase(
    private val authDatasource: SupabaseAuthDatasource,
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        authDatasource.signInAnonymously()
    }
}
