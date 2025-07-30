package com.pointlessapps.granite.navigation

import kotlinx.serialization.Serializable

internal sealed interface Route {

    companion object {
        val routesWithBottomBar = listOf(
            Home::class,
            Editor.Note::class,
            Editor.NewNote::class,
        )
    }

    @Serializable
    data object Login : Route

    @Serializable
    data object Home : Route

    @Serializable
    sealed interface Editor : Route {
        @Serializable
        data object DailyNote : Editor

        @Serializable
        data class Note(val id: Int) : Editor

        @Serializable
        data class NewNote(val parentId: Int?) : Editor
    }

    @Serializable
    data object Search : Route
}
