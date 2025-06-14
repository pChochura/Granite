package com.pointlessapps.granite.local.datasource.note.dao

import androidx.room.Dao
import androidx.room.Query
import com.pointlessapps.granite.local.datasource.note.entity.NoteEntity

@Dao
internal interface NoteDao {
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getById(id: Int): NoteEntity?

    @Query("SELECT * FROM notes")
    suspend fun getAll(): List<NoteEntity>

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

    @Query("UPDATE notes SET is_deleted = :deleted, updated_at = :currentTimestamp WHERE id in (:ids)")
    suspend fun markAsDeleted(ids: Set<Int>, deleted: Boolean, currentTimestamp: String)

    @Query("UPDATE notes SET parent_id = NULL, updated_at = :currentTimestamp WHERE id = :id")
    suspend fun removeParent(id: Int, currentTimestamp: String)

    @Query("DELETE FROM notes WHERE id IN (:ids)")
    suspend fun delete(ids: Set<Int>)
}
