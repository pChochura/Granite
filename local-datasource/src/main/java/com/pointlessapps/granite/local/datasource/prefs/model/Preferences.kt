package com.pointlessapps.granite.local.datasource.prefs.model

import kotlinx.serialization.Serializable

@Serializable
internal data class Preferences(
    val lastOpenedFileId: Int? = null,
    val itemsOrderTypeIndex: Int = 0,
    val dailyNotesEnabled: Boolean = true,
    val dailyNotesFolderName: String = "Journal",
    val dailyNotesFolderId: Int? = null,
)
