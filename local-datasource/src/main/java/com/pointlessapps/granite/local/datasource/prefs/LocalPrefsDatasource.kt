package com.pointlessapps.granite.local.datasource.prefs

import androidx.datastore.core.DataStore
import com.pointlessapps.granite.local.datasource.prefs.model.Preferences
import kotlinx.coroutines.flow.firstOrNull

interface LocalPrefsDatasource {
    suspend fun setLastOpenedFileId(id: Int?)
    suspend fun getLastOpenedFileId(): Int?

    suspend fun setItemsOrderTypeIndex(orderTypeIndex: Int)
    suspend fun getItemsOrderTypeIndex(): Int

    suspend fun getDailyNotesEnabled(): Boolean
    suspend fun setDailyNotesEnabled(enabled: Boolean)
    suspend fun getDailyNotesFolderName(): String
    suspend fun setDailyNotesFolderName(name: String)
    suspend fun getDailyNotesFolderId(): Int?
    suspend fun setDailyNotesFolderId(id: Int?)
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

    override suspend fun getDailyNotesEnabled() =
        prefsDataStore.data.firstOrNull()?.dailyNotesEnabled ?: true

    override suspend fun setDailyNotesEnabled(enabled: Boolean) {
        prefsDataStore.updateData { prefs ->
            prefs.copy(dailyNotesEnabled = enabled)
        }
    }

    override suspend fun getDailyNotesFolderName() =
        prefsDataStore.data.firstOrNull()?.dailyNotesFolderName ?: "Journal"

    override suspend fun setDailyNotesFolderName(name: String) {
        prefsDataStore.updateData { prefs ->
            prefs.copy(dailyNotesFolderName = name)
        }
    }

    override suspend fun getDailyNotesFolderId() =
        prefsDataStore.data.firstOrNull()?.dailyNotesFolderId

    override suspend fun setDailyNotesFolderId(id: Int?) {
        prefsDataStore.updateData { prefs ->
            prefs.copy(dailyNotesFolderId = id)
        }
    }
}
