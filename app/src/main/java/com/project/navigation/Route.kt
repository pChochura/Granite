package com.project.navigation

import kotlinx.serialization.Serializable

internal sealed interface Route {

    @Serializable
    data object Home : Route
}
