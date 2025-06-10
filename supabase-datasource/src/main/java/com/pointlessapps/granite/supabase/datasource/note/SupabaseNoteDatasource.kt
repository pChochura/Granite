package com.pointlessapps.granite.supabase.datasource.note

import com.pointlessapps.granite.supabase.datasource.note.model.Note
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

interface SupabaseNoteDatasource {
    suspend fun getById(id: Int): Note?
    suspend fun getAll(): List<Note>
}

internal class SupabaseNoteDatasourceImpl(
    private val client: SupabaseClient,
) : SupabaseNoteDatasource {

    override suspend fun getById(id: Int) = client
        .from(NOTES_TABLE_NAME)
        .select { filter { eq("id", id) } }
        .decodeSingleOrNull<Note>()

    override suspend fun getAll() = client
        .from(NOTES_TABLE_NAME)
        .select(Columns.list("id", "parent_id", "created_at", "updated_at", "name"))
        .decodeList<Note>()
}
