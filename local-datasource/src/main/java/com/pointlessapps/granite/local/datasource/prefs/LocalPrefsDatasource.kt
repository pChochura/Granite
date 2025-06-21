package com.pointlessapps.granite.local.datasource.prefs

import androidx.datastore.core.DataStore
import com.pointlessapps.granite.local.datasource.prefs.model.Preferences
import kotlinx.coroutines.flow.firstOrNull

interface LocalPrefsDatasource {
    suspend fun setLastOpenedFileId(id: Int?)
    suspend fun getLastOpenedFileId(): Int?

    suspend fun setItemsOrderTypeIndex(orderTypeIndex: Int)
    suspend fun getItemsOrderTypeIndex(): Int
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

    override suspend fun setItemsOrderTypeIndex(orderTypeIndex: Int) {
        prefsDataStore.updateData { prefs ->
            prefs.copy(itemsOrderTypeIndex = orderTypeIndex)
        }
    }

    override suspend fun getItemsOrderTypeIndex() =
        prefsDataStore.data.firstOrNull()?.itemsOrderTypeIndex ?: 0
}
