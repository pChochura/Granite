package com.pointlessapps.granite.supabase.datasource.auth

import com.pointlessapps.granite.datasource.auth.AuthDatasource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken

internal class SupabaseAuthDatasource(
    private val client: SupabaseClient,
) : AuthDatasource {
    override suspend fun initialize() {
        client.auth.awaitInitialization()
    }

    override fun isSignedIn(): Boolean {
        return client.auth.currentUserOrNull() != null
    }

    override suspend fun signInWithGoogle(googleIdToken: String) {
        client.auth.signInWith(IDToken) {
            idToken = googleIdToken
            provider = Google
        }
    }

    override suspend fun signIn(email: String, password: String) {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signInAnonymously() {
        client.auth.signInAnonymously()
    }
}
