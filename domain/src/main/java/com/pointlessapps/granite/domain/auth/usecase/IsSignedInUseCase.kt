package com.pointlessapps.granite.domain.auth.usecase

import com.pointlessapps.granite.supabase.datasource.auth.SupabaseAuthDatasource

class IsSignedInUseCase(
    private val authDatasource: SupabaseAuthDatasource,
) {
    operator fun invoke() = authDatasource.isSignedIn()
}
