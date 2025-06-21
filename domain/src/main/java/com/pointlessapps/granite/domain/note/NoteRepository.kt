package com.pointlessapps.granite.domain.note

import com.pointlessapps.granite.domain.mapper.fromLocal
import com.pointlessapps.granite.domain.model.Note
import com.pointlessapps.granite.local.datasource.note.LocalNoteDatasource
import com.pointlessapps.granite.local.datasource.note.entity.NoteEntity
import com.pointlessapps.granite.supabase.datasource.note.SupabaseNoteDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface NoteRepository {
    fun getById(id: Int): Flow<Note?>
    fun getAll(): Flow<List<Note>>

    fun update(id: Int, name: String, content: String?, parentId: Int?): Flow<Note>
    fun create(name: String, content: String?, parentId: Int?): Flow<List<Note>>

    fun markAsDeleted(ids: List<Int>, deleted: Boolean): Flow<Unit>
    fun delete(ids: List<Int>): Flow<Unit>

    fun duplicate(ids: List<Int>): Flow<List<Note>>

    fun move(id: Int, newParentId: Int?): Flow<Unit>
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

    override fun update(id: Int, name: String, content: String?, parentId: Int?) = flow {
        val entity = localDatasource.update(id, name, content, parentId)
            ?: throw NullPointerException("NoteEntity with id ($id) could not be updated")

        emit(entity.fromLocal())
    }.flowOn(Dispatchers.IO)

    override fun create(name: String, content: String?, parentId: Int?) = flow {
        // Create parent folders
        var currentParentId: Int? = parentId
        val createdFolders = mutableListOf<NoteEntity>()
        if (name.contains('/')) {
            val segments = name.split('/')
            // Skip the last element, which is the file name
            for (i in 0 until segments.lastIndex) {
                val entity = localDatasource.create(segments[i], null, currentParentId)
                    ?: throw NullPointerException("NoteEntity could not be created")
                createdFolders.add(entity)
                currentParentId = entity.id
            }
        }

        val entity = localDatasource.create(name.substringAfterLast('/'), content, currentParentId)
            ?: throw NullPointerException("NoteEntity could not be created")

        emit((createdFolders + entity).map { it.fromLocal() })
    }.flowOn(Dispatchers.IO)

    override fun markAsDeleted(ids: List<Int>, deleted: Boolean) = flow {
        emit(localDatasource.markAsDeleted(ids, deleted))
    }.flowOn(Dispatchers.IO)

    override fun delete(ids: List<Int>) = flow {
        emit(localDatasource.delete(ids))
    }.flowOn(Dispatchers.IO)

    override fun duplicate(ids: List<Int>) = flow {
        emit(localDatasource.duplicate(ids).map { it.fromLocal() })
    }.flowOn(Dispatchers.IO)

    override fun move(id: Int, newParentId: Int?) = flow {
        emit(localDatasource.move(id, newParentId))
    }.flowOn(Dispatchers.IO)
}
