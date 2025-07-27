package com.pointlessapps.granite.local.datasource.note

import com.pointlessapps.granite.local.datasource.note.dao.TagDao

interface LocalTagDatasource {

    /**
     * Gets the daily note tag id or creates it if it doesn't exist.
     */
    suspend fun getDailyNoteTagId(): Int
}

internal class LocalTagDatasourceImpl(
    private val tagDao: TagDao,
) : LocalTagDatasource {

    private var dailyNoteTag: Int = -1

    override suspend fun getDailyNoteTagId(): Int {
        if (dailyNoteTag == -1) {
            dailyNoteTag = tagDao.getBuiltInOrCreate(
                BuiltIntTagType.DAILY_NOTE,
                DAILY_NOTE_TAG_NAME,
                DAILY_NOTE_TAG_COLOR,
            ).toInt()
        }

        return dailyNoteTag
    }

    companion object {
        // TODO move to prefs and allow editing
        private const val DAILY_NOTE_TAG_NAME = "daily"
        private const val DAILY_NOTE_TAG_COLOR = 12631426

        enum class BuiltIntTagType {
            DAILY_NOTE
        }
    }
}
