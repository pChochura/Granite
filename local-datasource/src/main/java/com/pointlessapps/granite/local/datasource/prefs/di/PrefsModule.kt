package com.pointlessapps.granite.local.datasource.prefs.di

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.FileStorage
import androidx.datastore.core.Storage
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.pointlessapps.granite.local.datasource.prefs.LocalPrefsDatasource
import com.pointlessapps.granite.local.datasource.prefs.LocalPrefsDatasourceImpl
import com.pointlessapps.granite.local.datasource.prefs.PREFS_FILE
import com.pointlessapps.granite.local.datasource.prefs.PREFS_FILE_STORAGE
import com.pointlessapps.granite.local.datasource.prefs.model.Preferences
import com.pointlessapps.granite.local.datasource.prefs.model.PreferencesDefaults
import com.pointlessapps.granite.local.datasource.utils.JsonSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

internal val prefsModule = module {
    single<Storage<Preferences>>(named(PREFS_FILE_STORAGE)) {
        FileStorage(
            serializer = JsonSerializer(Json, serializer(), PreferencesDefaults),
            produceFile = { File(get<Context>().filesDir, PREFS_FILE) },
        )
    }

    single<LocalPrefsDatasource> {
        LocalPrefsDatasourceImpl(
            prefsDataStore = DataStoreFactory.create(
                storage = get(named(PREFS_FILE_STORAGE)),
                corruptionHandler = ReplaceFileCorruptionHandler {
                    it.printStackTrace()

                    return@ReplaceFileCorruptionHandler PreferencesDefaults
                },
            ),
        )
    }
}
