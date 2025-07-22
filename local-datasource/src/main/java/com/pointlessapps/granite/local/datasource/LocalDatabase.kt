package com.pointlessapps.granite.local.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pointlessapps.granite.local.datasource.note.dao.NoteDao
import com.pointlessapps.granite.local.datasource.note.dao.TagDao
import com.pointlessapps.granite.local.datasource.note.entity.NoteEntity
import com.pointlessapps.granite.local.datasource.note.entity.NoteTagCrossRef
import com.pointlessapps.granite.local.datasource.note.entity.TagEntity

@Database(
    entities = [NoteEntity::class, TagEntity::class, NoteTagCrossRef::class],
    version = 1,
)
internal abstract class LocalDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun tagDao(): TagDao
}
