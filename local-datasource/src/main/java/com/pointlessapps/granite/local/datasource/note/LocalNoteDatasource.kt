package com.pointlessapps.granite.local.datasource.note

import com.pointlessapps.granite.local.datasource.note.dao.NoteDao
import com.pointlessapps.granite.local.datasource.note.entity.NoteEntity
import com.pointlessapps.granite.local.datasource.note.entity.NoteEntityParentIdPartial
import com.pointlessapps.granite.local.datasource.note.entity.NoteEntityPartial
import com.pointlessapps.granite.local.datasource.note.utils.getCurrentTimestamp

interface LocalNoteDatasource {
    suspend fun getById(id: Int): NoteEntity?
    suspend fun getAll(): List<NoteEntity>

    suspend fun update(id: Int, name: String, content: String?, parentId: Int?): NoteEntity?
    suspend fun create(name: String, content: String?, parentId: Int?): NoteEntity?

    suspend fun markAsDeleted(ids: List<Int>, deleted: Boolean)
    suspend fun delete(ids: List<Int>)

    suspend fun duplicate(ids: List<Int>): List<NoteEntity>

    suspend fun move(id: Int, newParentId: Int)
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

    override suspend fun markAsDeleted(ids: List<Int>, deleted: Boolean) =
        noteDao.markAsDeleted(ids, deleted, getCurrentTimestamp())

    override suspend fun delete(ids: List<Int>) {
        noteDao.delete(ids)
        noteDao.removeParents(ids, getCurrentTimestamp())
    }

    override suspend fun duplicate(ids: List<Int>): List<NoteEntity> {
        val currentTimestamp = getCurrentTimestamp()
        val originalNotes = noteDao.getByIds(ids)
        val idMapping = mutableMapOf<Int, Int>()
        val newNotesToInsert = noteDao.getByIds(ids).map { entity ->
            NoteEntityPartial(
                parentId = entity.parentId,
                name = entity.name,
                content = entity.content,
                updatedAt = currentTimestamp,
                createdAt = currentTimestamp,
            )
        }

        val newIds = noteDao.insertMany(newNotesToInsert).map(Long::toInt)
        originalNotes.forEachIndexed { index, originalNote ->
            idMapping[originalNote.id] = newIds[index]
        }

        val notesToUpdateParentId = mutableListOf<NoteEntityParentIdPartial>()
        originalNotes.forEachIndexed { index, originalNote ->
            val newNoteId = newIds[index]
            val originalParentId = originalNote.parentId
            val newParentId = originalParentId?.let { idMapping[it] }
            if (newParentId != null && newParentId != originalParentId) {
                notesToUpdateParentId.add(
                    NoteEntityParentIdPartial(
                        id = newNoteId,
                        parentId = newParentId,
                    ),
                )
            }
        }

        if (notesToUpdateParentId.isNotEmpty()) {
            noteDao.updateManyParentIds(notesToUpdateParentId)
        }

        return noteDao.getByIds(newIds)
    }

    override suspend fun move(id: Int, newParentId: Int) =
        noteDao.move(id, newParentId, getCurrentTimestamp())
}
