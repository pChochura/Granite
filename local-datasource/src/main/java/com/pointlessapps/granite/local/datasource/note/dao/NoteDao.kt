package com.pointlessapps.granite.local.datasource.note.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pointlessapps.granite.local.datasource.note.entity.NoteEntity
import com.pointlessapps.granite.local.datasource.note.entity.NoteEntityParentIdPartial
import com.pointlessapps.granite.local.datasource.note.entity.NoteEntityPartial
import com.pointlessapps.granite.local.datasource.note.entity.NoteWithTagsEntity

@Dao
internal interface NoteDao {
    @Transaction
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getById(id: Int): NoteWithTagsEntity?

    @Transaction
    @Query("SELECT * FROM notes WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Int>): List<NoteWithTagsEntity>

    @Transaction
    @Query("SELECT * FROM notes WHERE name = :name AND parent_id = :folderId")
    suspend fun findInFolderByName(name: String, folderId: Int?): NoteWithTagsEntity?

    @Transaction
    @Query("SELECT * FROM notes")
    suspend fun getAll(): List<NoteWithTagsEntity>

    @Query("UPDATE notes SET name = :name, updated_at = :currentTimestamp WHERE id = :id")
    suspend fun updateName(id: Int, name: String, currentTimestamp: String)

    @Query(
        "UPDATE notes " +
                "SET name = :name, content = :content, parent_id = :parentId, updated_at = :currentTimestamp " +
                "WHERE id = :id",
    )
    suspend fun update(
        id: Int,
        name: String,
        content: String?,
        parentId: Int?,
        currentTimestamp: String,
    )

    @Query(
        "INSERT INTO notes (name, content, parent_id, created_at, updated_at) " +
                "VALUES (:name, :content, :parentId, :currentTimestamp, :currentTimestamp)",
    )
    suspend fun insert(
        name: String,
        content: String?,
        parentId: Int?,
        currentTimestamp: String,
    ): Long

    @Insert(entity = NoteEntity::class)
    suspend fun insertMany(notes: List<NoteEntityPartial>): List<Long>

    @Update(entity = NoteEntity::class)
    suspend fun updateManyParentIds(notes: List<NoteEntityParentIdPartial>)

    @Query("UPDATE notes SET is_deleted = :deleted, updated_at = :currentTimestamp WHERE id in (:ids)")
    suspend fun markAsDeleted(ids: List<Int>, deleted: Boolean, currentTimestamp: String)

    @Query("DELETE FROM notes WHERE id IN (:ids)")
    suspend fun delete(ids: List<Int>)

    @Query("UPDATE notes SET parent_id = NULL, updated_at = :currentTimestamp WHERE parent_id in (:ids)")
    suspend fun resetParentIds(ids: List<Int>, currentTimestamp: String)

    @Query("UPDATE notes SET parent_id = :newParentId, updated_at = :currentTimestamp WHERE id = :id")
    suspend fun move(id: Int, newParentId: Int?, currentTimestamp: String)
}
