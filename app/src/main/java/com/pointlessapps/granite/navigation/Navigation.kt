package com.pointlessapps.granite.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pointlessapps.granite.MainViewModel
import com.pointlessapps.granite.editor.ui.EditorScreen
import com.pointlessapps.granite.home.ui.HomeScreen
import com.pointlessapps.granite.login.ui.LoginScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun Navigation(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel = koinViewModel(),
) {
    if (!mainViewModel.isInitialized) return

    NavHost(
        navController = navController,
        startDestination = if (mainViewModel.isSignedIn()) Route.Home else Route.Login,
    ) {
        composable<Route.Login> {
            LoginScreen(
                onNavigateTo = {
                    navController.navigate(
                        route = it,
                        builder = { popUpTo(Route.Login) { inclusive = true } },
                    )
                },
            )
        }
        composable<Route.Home> {
            HomeScreen(onNavigateTo = navController::navigate)
        }
        composable<Route.Editor>(typeMap = Route.Editor.Arg.typeMap) {
            val arg = it.toRoute<Route.Editor>().arg
            EditorScreen(
                viewModel = koinViewModel { parametersOf(arg) },
                onNavigateTo = navController::navigate,
            )
        }
        composable<Route.Search> {
        }
    }
}
