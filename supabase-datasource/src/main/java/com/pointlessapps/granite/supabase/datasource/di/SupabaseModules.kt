package com.pointlessapps.granite.supabase.datasource.di

import com.pointlessapps.granite.supabase.datasource.auth.di.authModule
import com.pointlessapps.granite.supabase.datasource.note.di.noteModule

val supabaseModules = listOf(authModule, noteModule)
