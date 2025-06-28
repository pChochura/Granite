package com.pointlessapps.granite.local.datasource.prefs.model

import kotlinx.serialization.Serializable

@Serializable
internal data class Preferences(
    val lastOpenedFileId: Int? = PreferencesDefaults.lastOpenedFileId,
    val itemsOrderTypeIndex: Int = PreferencesDefaults.itemsOrderTypeIndex,
    val dailyNotesEnabled: Boolean = PreferencesDefaults.dailyNotesEnabled,
    val dailyNotesFolderName: String = PreferencesDefaults.dailyNotesFolderName,
    val dailyNotesFolderId: Int? = PreferencesDefaults.dailyNotesFolderId,
    val dailyNotesNameFormat: String = PreferencesDefaults.dailyNotesNameFormat,
)

internal val PreferencesDefaults = Preferences(
    lastOpenedFileId = null,
    itemsOrderTypeIndex = 0,
    dailyNotesEnabled = true,
    dailyNotesFolderName = "Journal",
    dailyNotesFolderId = null,
    dailyNotesNameFormat = "dd.MM.yyyy",
)
