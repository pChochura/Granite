package com.pointlessapps.granite.navigation

import com.pointlessapps.granite.typemap.processor.GenerateTypeMap
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
    data class Editor(val arg: Arg) : Route {

        @GenerateTypeMap
        @Serializable
        sealed interface Arg {
            @Serializable
            data object NewDailyNote : Arg

            @Serializable
            data class NewNote(val parentId: Int?) : Arg

            @Serializable
            data class Note(val id: Int) : Arg
        }
    }

    @Serializable
    data object Search : Route
}
