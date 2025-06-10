package com.pointlessapps.granite.local.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pointlessapps.granite.local.datasource.note.dao.NoteDao
import com.pointlessapps.granite.local.datasource.note.entity.NoteEntity

@Database(
    entities = [NoteEntity::class],
    version = 1,
)
internal abstract class LocalDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
