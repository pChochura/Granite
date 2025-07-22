package com.pointlessapps.granite.navigation

import kotlinx.serialization.Serializable

internal sealed interface Route {

    companion object {
        val routesWithBottomBar = listOf(
            Home::class,
            Editor::class,
        )
    }

    @Serializable
    data object Login : Route

    @Serializable
    data object Home : Route

    @Serializable
    data class Editor(val itemId: Int? = null) : Route

    @Serializable
    data object DailyNote : Route

    @Serializable
    data object Search : Route
}
