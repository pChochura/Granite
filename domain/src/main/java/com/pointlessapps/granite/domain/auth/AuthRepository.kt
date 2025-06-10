package com.pointlessapps.granite.domain.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.pointlessapps.granite.supabase.datasource.auth.SupabaseAuthDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface AuthRepository {
    fun initialize(): Flow<Unit>

    fun isSignedIn(): Boolean
    fun signInWithGoogle(): Flow<Unit>
    fun signIn(email: String, password: String): Flow<Unit>
    fun signInAnonymously(): Flow<Unit>
}

internal class AuthRepositoryImpl(
    private val context: Context,
    private val authDatasource: SupabaseAuthDatasource,
    private val googleWebClientId: String,
) : AuthRepository {

    override fun initialize() = flow {
        emit(authDatasource.initialize())
    }.flowOn(Dispatchers.IO)

    override fun isSignedIn() = authDatasource.isSignedIn()

    override fun signInWithGoogle() = flow {
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
        emit(authDatasource.signInWithGoogle(googleIdToken))
    }.flowOn(Dispatchers.IO)

    override fun signIn(email: String, password: String) = flow {
        emit(authDatasource.signIn(email, password))
    }.flowOn(Dispatchers.IO)

    override fun signInAnonymously() = flow {
        emit(authDatasource.signInAnonymously())
    }.flowOn(Dispatchers.IO)
}
