package com.pointlessapps.granite.supabase.datasource.note

import com.pointlessapps.granite.datasource.note.NoteDatasource
import com.pointlessapps.granite.datasource.note.model.Note
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

internal class SupabaseNoteDatasource(
    private val client: SupabaseClient,
) : NoteDatasource {

    override suspend fun getNote(id: Int) = client
        .from(NOTES_TABLE_NAME)
        .select { filter { eq("id", id) } }
        .decodeSingleOrNull<Note>()

    override suspend fun getNotes() = client
        .from(NOTES_TABLE_NAME)
        .select(Columns.list("id", "parent_id", "created_at", "updated_at", "name"))
        .decodeList<Note>()
}
