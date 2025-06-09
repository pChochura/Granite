package com.pointlessapps.granite.datasource.auth

interface AuthDatasource {
    suspend fun isSignedIn(): Boolean

    suspend fun signIn(email: String, password: String)
    suspend fun signInAnonymously()
}
