package com.pointlessapps.granite.local.datasource.note.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.pointlessapps.granite.local.datasource.note.LocalTagDatasourceImpl
import com.pointlessapps.granite.local.datasource.note.entity.TagEntity

@Dao
internal interface TagDao {

    @Transaction
    suspend fun getBuiltInOrCreate(
        builtIntTagType: LocalTagDatasourceImpl.Companion.BuiltIntTagType,
        name: String,
        color: Int,
    ): Long {
        val tag = findByName(name, isBuiltIn = true, isAutomatic = true)
        if (tag != null) {
            return tag.id.toLong()
        }

        return insert(name, color, isBuiltIn = true, isAutomatic = true)
    }

    @Query("SELECT * FROM tags")
    suspend fun getAll(): List<TagEntity>

    @Query("SELECT * FROM tags WHERE name = :name AND is_built_in = :isBuiltIn AND is_automatic = :isAutomatic")
    suspend fun findByName(name: String, isBuiltIn: Boolean, isAutomatic: Boolean): TagEntity?

    @Query("INSERT INTO tags (name, color, is_built_in, is_automatic) VALUES (:name, :color, :isBuiltIn, :isAutomatic)")
    suspend fun insert(name: String, color: Int, isBuiltIn: Boolean, isAutomatic: Boolean): Long
}
