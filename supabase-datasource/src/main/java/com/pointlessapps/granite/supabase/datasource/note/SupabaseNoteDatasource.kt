package com.pointlessapps.granite.supabase.datasource.note

import com.pointlessapps.granite.datasource.note.NoteDatasource
import com.pointlessapps.granite.datasource.note.model.Note
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

internal class SupabaseNoteDatasource(
    private val client: SupabaseClient,
) : NoteDatasource {
    override suspend fun getNotes(): List<Note> {
        return client.from(NOTES_TABLE_NAME)
            .select().decodeList<Note>()
    }
}
