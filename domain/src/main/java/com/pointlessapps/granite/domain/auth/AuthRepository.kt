package com.pointlessapps.granite.domain.auth

import com.pointlessapps.granite.datasource.auth.AuthDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface AuthRepository {
    fun isSignedIn(): Flow<Boolean>

    fun signIn(email: String, password: String): Flow<Unit>
    fun signInAnonymously(): Flow<Unit>
}

internal class AuthRepositoryImpl(
    private val authDatasource: AuthDatasource,
) : AuthRepository {
    override fun isSignedIn() = flow {
        emit(authDatasource.isSignedIn())
    }.flowOn(Dispatchers.IO)

    override fun signIn(email: String, password: String) = flow {
        emit(authDatasource.signIn(email, password))
    }.flowOn(Dispatchers.IO)

    override fun signInAnonymously() = flow {
        emit(authDatasource.signInAnonymously())
    }.flowOn(Dispatchers.IO)
}
