package com.pointlessapps.granite.domain.note

import com.pointlessapps.granite.domain.note.mapper.fromLocal
import com.pointlessapps.granite.domain.note.model.Note
import com.pointlessapps.granite.local.datasource.note.LocalNoteDatasource
import com.pointlessapps.granite.supabase.datasource.note.SupabaseNoteDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface NoteRepository {
    fun getById(id: Int): Flow<Note?>
    fun getAll(): Flow<List<Note>>

    fun upsert(id: Int?, name: String, content: String, parentId: Int?): Flow<Note>
}

internal class NoteRepositoryImpl(
    private val localDatasource: LocalNoteDatasource,
    private val remoteDatasource: SupabaseNoteDatasource,
) : NoteRepository {
    override fun getById(id: Int) = flow {
        emit(localDatasource.getById(id)?.fromLocal())
    }.flowOn(Dispatchers.IO)

    override fun getAll() = flow {
        emit(localDatasource.getAll().map { it.fromLocal() })
    }.flowOn(Dispatchers.IO)

    override fun upsert(id: Int?, name: String, content: String, parentId: Int?) = flow {
        val entity = localDatasource.upsert(id, name, content, parentId)
            ?: throw NullPointerException("NoteEntity could not be created")

        emit(entity.fromLocal())
    }.flowOn(Dispatchers.IO)
}
