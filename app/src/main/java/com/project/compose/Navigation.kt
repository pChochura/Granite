package com.project.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.project.compose.home.HomeScreen
import com.project.compose.ui.theme.Route
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.rememberNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun NavHost(
    navController: NavController<Route> = rememberNavController(
        startDestination = Route.Home,
    ),
) {
    NavBackHandler(navController = navController)
    AnimatedNavHost(controller = navController) {
        when (it) {
            Route.Home -> HomeScreen()
        }
    }
}
