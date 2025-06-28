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

    suspend fun setDailyNotesFolderName(name: String)
    suspend fun getDailyNotesFolderName(): String

    suspend fun setDailyNotesFolderId(id: Int?)
    suspend fun getDailyNotesFolderId(): Int?

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

    override suspend fun getDailyNotesEnabled() =
        prefsDataStore.data.firstOrNull()?.dailyNotesEnabled
            ?: PreferencesDefaults.dailyNotesEnabled

    override suspend fun setDailyNotesFolderName(name: String) {
        prefsDataStore.updateData { prefs ->
            prefs.copy(dailyNotesFolderName = name)
        }
    }

    override suspend fun getDailyNotesFolderName() =
        prefsDataStore.data.firstOrNull()?.dailyNotesFolderName
            ?: PreferencesDefaults.dailyNotesFolderName

    override suspend fun setDailyNotesFolderId(id: Int?) {
        prefsDataStore.updateData { prefs ->
            prefs.copy(dailyNotesFolderId = id)
        }
    }

    override suspend fun getDailyNotesFolderId() =
        prefsDataStore.data.firstOrNull()?.dailyNotesFolderId

    override suspend fun setDailyNotesNameFormat(format: String) {
        prefsDataStore.updateData { prefs ->
            prefs.copy(dailyNotesNameFormat = format)
        }
    }

    override suspend fun getDailyNotesNameFormat() =
        prefsDataStore.data.firstOrNull()?.dailyNotesNameFormat
            ?: PreferencesDefaults.dailyNotesNameFormat
}
