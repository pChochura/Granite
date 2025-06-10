package com.pointlessapps.granite.datasource.auth

interface AuthDatasource {
    suspend fun initialize()

    fun isSignedIn(): Boolean
    suspend fun signInWithGoogle(googleIdToken: String)
    suspend fun signIn(email: String, password: String)
    suspend fun signInAnonymously()
}
