package com.pointlessapps.obsidian_mini.navigation

import kotlinx.serialization.Serializable

internal sealed interface Route {

    @Serializable
    data object Home : Route
}
