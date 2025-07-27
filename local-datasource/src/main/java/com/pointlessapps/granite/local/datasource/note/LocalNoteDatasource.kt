package com.pointlessapps.granite.local.datasource.note

import com.pointlessapps.granite.local.datasource.note.dao.NoteDao
import com.pointlessapps.granite.local.datasource.note.entity.NoteWithTagsEntity
import com.pointlessapps.granite.local.datasource.note.entity.partials.NoteEntityParentIdPartial
import com.pointlessapps.granite.local.datasource.note.entity.partials.NoteEntityPartial
import com.pointlessapps.granite.local.datasource.note.utils.getCurrentTimestamp

interface LocalNoteDatasource {
    suspend fun getById(id: Int): NoteWithTagsEntity?
    suspend fun getAll(): List<NoteWithTagsEntity>

    suspend fun findInFolderByName(name: String, folderId: Int?): NoteWithTagsEntity?

    suspend fun updateName(id: Int, name: String): NoteWithTagsEntity?
    suspend fun update(id: Int, name: String, content: String?, parentId: Int?): NoteWithTagsEntity?
    suspend fun create(name: String, content: String?, parentId: Int?): NoteWithTagsEntity?

    suspend fun markAsDeleted(ids: List<Int>, deleted: Boolean): List<NoteWithTagsEntity>
    suspend fun delete(ids: List<Int>)

    suspend fun duplicate(ids: List<Int>): List<NoteWithTagsEntity>

    suspend fun move(id: Int, newParentId: Int?): NoteWithTagsEntity?

    suspend fun assignTags(id: Int, tagIds: List<Int>): NoteWithTagsEntity?
}

internal class LocalNoteDatasourceImpl(
    private val noteDao: NoteDao,
) : LocalNoteDatasource {
    override suspend fun getById(id: Int) = noteDao.getById(id)
    override suspend fun getAll() = noteDao.getAll()

    override suspend fun findInFolderByName(name: String, folderId: Int?) =
        noteDao.findInFolderByName(name, folderId)

    override suspend fun updateName(id: Int, name: String): NoteWithTagsEntity? {
        noteDao.updateName(id, name, getCurrentTimestamp())

        return noteDao.getById(id)
    }

    override suspend fun update(
        id: Int,
        name: String,
        content: String?,
        parentId: Int?,
    ): NoteWithTagsEntity? {
        noteDao.update(id, name, content, parentId, getCurrentTimestamp())

        return noteDao.getById(id)
    }

    override suspend fun create(
        name: String,
        content: String?,
        parentId: Int?,
    ): NoteWithTagsEntity? {
        val currentTimestamp = getCurrentTimestamp()
        val noteId = noteDao.insert(name, content, parentId, currentTimestamp).toInt()

        return noteDao.getById(noteId)
    }

    override suspend fun markAsDeleted(ids: List<Int>, deleted: Boolean): List<NoteWithTagsEntity> {
        noteDao.markAsDeleted(ids, deleted, getCurrentTimestamp())

        return noteDao.getByIds(ids)
    }

    override suspend fun delete(ids: List<Int>) {
        noteDao.delete(ids)
        noteDao.resetParentIds(ids, getCurrentTimestamp())
    }

    override suspend fun duplicate(ids: List<Int>): List<NoteWithTagsEntity> {
        val currentTimestamp = getCurrentTimestamp()
        val originalNotes = noteDao.getByIds(ids)
        val idMapping = mutableMapOf<Int, Int>()
        val newNotesToInsert = noteDao.getByIds(ids).map { entity ->
            NoteEntityPartial(
                parentId = entity.note.parentId,
                name = entity.note.name,
                content = entity.note.content,
                updatedAt = currentTimestamp,
                createdAt = currentTimestamp,
            )
        }

        val newIds = noteDao.insertMany(newNotesToInsert).map(Long::toInt)
        originalNotes.forEachIndexed { index, originalNote ->
            idMapping[originalNote.note.id] = newIds[index]
        }

        val notesToUpdateParentId = mutableListOf<NoteEntityParentIdPartial>()
        originalNotes.forEachIndexed { index, originalNote ->
            val newNoteId = newIds[index]
            val originalParentId = originalNote.note.parentId
            val newParentId = originalParentId?.let { idMapping[it] }
            if (newParentId != null && newParentId != originalParentId) {
                notesToUpdateParentId.add(
                    NoteEntityParentIdPartial(
                        id = newNoteId,
                        parentId = newParentId,
                        updatedAt = currentTimestamp,
                    ),
                )
            }
        }

        if (notesToUpdateParentId.isNotEmpty()) {
            noteDao.updateManyParentIds(notesToUpdateParentId)
        }

        return noteDao.getByIds(newIds)
    }

    override suspend fun move(id: Int, newParentId: Int?): NoteWithTagsEntity? {
        noteDao.move(id, newParentId, getCurrentTimestamp())

        return noteDao.getById(id)
    }

    override suspend fun assignTags(id: Int, tagIds: List<Int>): NoteWithTagsEntity? {
        noteDao.updateTags(id, tagIds, getCurrentTimestamp())

        return noteDao.getById(id)
    }
}
