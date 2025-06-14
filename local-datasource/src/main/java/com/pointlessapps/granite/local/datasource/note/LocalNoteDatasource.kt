package com.pointlessapps.granite.local.datasource.note

import com.pointlessapps.granite.local.datasource.note.dao.NoteDao
import com.pointlessapps.granite.local.datasource.note.entity.NoteEntity
import com.pointlessapps.granite.local.datasource.note.utils.getCurrentTimestamp

interface LocalNoteDatasource {
    suspend fun getById(id: Int): NoteEntity?
    suspend fun getAll(): List<NoteEntity>

    suspend fun update(id: Int, name: String, content: String?, parentId: Int?): NoteEntity?
    suspend fun create(name: String, content: String?, parentId: Int?): NoteEntity?

    suspend fun markAsDeleted(ids: Set<Int>, deleted: Boolean)
    suspend fun delete(ids: Set<Int>)
}

internal class LocalNoteDatasourceImpl(
    private val noteDao: NoteDao,
) : LocalNoteDatasource {
    override suspend fun getById(id: Int) = noteDao.getById(id)
    override suspend fun getAll() = noteDao.getAll()

    override suspend fun update(
        id: Int,
        name: String,
        content: String?,
        parentId: Int?,
    ): NoteEntity? {
        noteDao.update(id, name, content, parentId, getCurrentTimestamp())

        return noteDao.getById(id)
    }

    override suspend fun create(name: String, content: String?, parentId: Int?): NoteEntity? {
        val currentTimestamp = getCurrentTimestamp()
        val noteId = noteDao.insert(name, content, parentId, currentTimestamp).toInt()

        return noteDao.getById(noteId)
    }

    override suspend fun markAsDeleted(ids: Set<Int>, deleted: Boolean) {
        noteDao.markAsDeleted(ids, deleted, getCurrentTimestamp())
        if (deleted) {
            noteDao.removeParent(ids.first(), getCurrentTimestamp())
        }
    }

    override suspend fun delete(ids: Set<Int>) = noteDao.delete(ids)
}
