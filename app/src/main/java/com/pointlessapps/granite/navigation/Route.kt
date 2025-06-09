package com.pointlessapps.granite.navigation

import kotlinx.serialization.Serializable

internal sealed interface Route {

    @Serializable
    data object Login : Route

    @Serializable
    data object Home : Route
}
