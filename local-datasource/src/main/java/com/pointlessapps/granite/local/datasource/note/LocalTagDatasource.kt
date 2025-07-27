package com.pointlessapps.granite.local.datasource.note

import com.pointlessapps.granite.local.datasource.note.dao.TagDao
import com.pointlessapps.granite.local.datasource.note.entity.TagEntity

interface LocalTagDatasource {

    /**
     * Gets the daily note tag or creates it if it doesn't exist.
     */
    fun getDailyNoteTag(): TagEntity
}

internal class LocalTagDatasourceImpl(
    private val tagDao: TagDao,
) : LocalTagDatasource {
    override fun getDailyNoteTag(): TagEntity {
        TODO("Not yet implemented")
    }
}
