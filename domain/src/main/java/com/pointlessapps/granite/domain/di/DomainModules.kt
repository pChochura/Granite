package com.pointlessapps.granite.domain.di

import com.pointlessapps.granite.domain.auth.di.authModule
import com.pointlessapps.granite.domain.note.di.noteModule
import com.pointlessapps.granite.domain.prefs.di.prefsModule

val domainModules = listOf(authModule, noteModule, prefsModule)
