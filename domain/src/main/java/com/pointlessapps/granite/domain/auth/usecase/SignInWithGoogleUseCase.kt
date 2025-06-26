package com.pointlessapps.granite.domain.auth.usecase

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.pointlessapps.granite.supabase.datasource.auth.SupabaseAuthDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SignInWithGoogleUseCase(
    private val authDatasource: SupabaseAuthDatasource,
    private val googleWebClientId: String,
) {
    suspend operator fun invoke(context: Context) = withContext(Dispatchers.IO) {
        val credentialManager = CredentialManager.create(context)
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(googleWebClientId)
                    .build(),
            )
            .build()

        val result = credentialManager.getCredential(context, request)
        val googleIdToken = GoogleIdTokenCredential.createFrom(result.credential.data).idToken
        authDatasource.signInWithGoogle(googleIdToken)
    }
}
