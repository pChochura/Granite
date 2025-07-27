package com.pointlessapps.granite.local.datasource.prefs

import androidx.datastore.core.DataStore
import com.pointlessapps.granite.local.datasource.prefs.model.Preferences
import com.pointlessapps.granite.local.datasource.prefs.model.PreferencesDefaults
import kotlinx.coroutines.flow.firstOrNull

interface LocalPrefsDatasource {
    suspend fun setLastOpenedFileId(id: Int?)
    suspend fun getLastOpenedFileId(): Int?

    suspend fun setItemsOrderTypeIndex(orderTypeIndex: Int)
    suspend fun getItemsOrderTypeIndex(): Int

    suspend fun setDailyNotesEnabled(enabled: Boolean)
    suspend fun getDailyNotesEnabled(): Boolean

    suspend fun getDailyNotesFolderId(): Int

    suspend fun setDailyNotesNameFormat(format: String)
    suspend fun getDailyNotesNameFormat(): String
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
        prefsDataStore.data.firstOrNull()?.itemsOrderTypeIndex
            ?: PreferencesDefaults.itemsOrderTypeIndex

    override suspend fun setDailyNotesEnabled(enabled: Boolean) {
        prefsDataStore.updateData { prefs ->
            prefs.copy(dailyNotesEnabled = enabled)
        }
    }

    // Group those notes in a virtual folder preventing them from being displayed with the rest
    // of the notes
    override suspend fun getDailyNotesFolderId() = -1

    override suspend fun getDailyNotesEnabled() =
        prefsDataStore.data.firstOrNull()?.dailyNotesEnabled
            ?: PreferencesDefaults.dailyNotesEnabled

    override suspend fun setDailyNotesNameFormat(format: String) {
        prefsDataStore.updateData { prefs ->
            prefs.copy(dailyNotesNameFormat = format)
        }
    }

    override suspend fun getDailyNotesNameFormat() =
        prefsDataStore.data.firstOrNull()?.dailyNotesNameFormat
            ?: PreferencesDefaults.dailyNotesNameFormat
}
