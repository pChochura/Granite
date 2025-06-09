package com.pointlessapps.granite.supabase.datasource.auth

import com.pointlessapps.granite.datasource.auth.AuthDatasource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

internal class SupabaseAuthDatasource(
    private val client: SupabaseClient,
) : AuthDatasource {
    override suspend fun isSignedIn(): Boolean {
        client.auth.awaitInitialization()

        return client.auth.currentUserOrNull() != null
    }

    override suspend fun signIn(email: String, password: String) {
        if (isSignedIn()) return

        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signInAnonymously() {
        if (isSignedIn()) return

        client.auth.signInAnonymously()
    }
}
