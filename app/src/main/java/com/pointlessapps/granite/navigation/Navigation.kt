package com.pointlessapps.granite.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pointlessapps.granite.home.ui.HomeScreen
import com.pointlessapps.granite.login.ui.LoginScreen

@Composable
internal fun Navigation(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Route.Login,
    ) {
        composable<Route.Login> {
            LoginScreen(onNavigateTo = navController::navigate)
        }
        composable<Route.Home> {
            HomeScreen(onNavigateTo = navController::navigate)
        }
    }
}
