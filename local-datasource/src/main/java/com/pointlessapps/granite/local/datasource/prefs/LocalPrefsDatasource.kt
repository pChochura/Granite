package com.pointlessapps.granite.local.datasource.prefs

import androidx.datastore.core.DataStore
import com.pointlessapps.granite.local.datasource.prefs.model.Preferences
import kotlinx.coroutines.flow.firstOrNull

interface LocalPrefsDatasource {
    suspend fun setLastOpenedFileId(id: Int?)
    suspend fun getLastOpenedFileId(): Int?
}

internal class LocalPrefsDatasourceImpl(
    private val prefsDataStore: DataStore<Preferences>,
) : LocalPrefsDatasource {
    override suspend fun setLastOpenedFileId(id: Int?) {
        prefsDataStore.updateData { prefs ->
            prefs.copy(lastOpenedFileId = id)
        }
    }

    override suspend fun getLastOpenedFileId() =
        prefsDataStore.data.firstOrNull()?.lastOpenedFileId
}
