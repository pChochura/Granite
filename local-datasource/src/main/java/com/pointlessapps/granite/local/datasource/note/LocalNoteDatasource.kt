package com.pointlessapps.granite.local.datasource.note

import com.pointlessapps.granite.local.datasource.note.dao.NoteDao
import com.pointlessapps.granite.local.datasource.note.entity.NoteEntity
import com.pointlessapps.granite.local.datasource.note.utils.getCurrentTimestamp

interface LocalNoteDatasource {
    suspend fun getById(id: Int): NoteEntity?
    suspend fun getAll(): List<NoteEntity>

    suspend fun upsert(id: Int?, name: String, content: String, parentId: Int?): Int
}

internal class LocalNoteDatasourceImpl(
    private val noteDao: NoteDao,
) : LocalNoteDatasource {
    override suspend fun getById(id: Int) = noteDao.getById(id)
    override suspend fun getAll() = noteDao.getAll()

    override suspend fun upsert(id: Int?, name: String, content: String, parentId: Int?): Int {
        val currentTimestamp = getCurrentTimestamp()

        if (id != null) {
            noteDao.upsert(id, name, content, parentId, currentTimestamp)

            return id
        } else {
            return noteDao.insert(name, content, parentId, currentTimestamp).toInt()
        }
    }
}
