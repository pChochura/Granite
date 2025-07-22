package com.pointlessapps.granite.local.datasource.note.dao

import androidx.room.Dao
import androidx.room.Query
import com.pointlessapps.granite.local.datasource.note.entity.TagEntity

@Dao
internal interface TagDao {
    @Query("SELECT * FROM tags")
    suspend fun getAll(): List<TagEntity>

    @Query("SELECT * FROM tags WHERE name = :name")
    suspend fun findByName(name: String): TagEntity?

    @Query("INSERT INTO tags (name, color, isBuiltIn) VALUES (:name, :color, :isBuiltIn)")
    suspend fun insert(name: String, color: Int, isBuiltIn: Boolean): Long
}
