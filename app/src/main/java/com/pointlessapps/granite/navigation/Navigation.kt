package com.pointlessapps.granite.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
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
        // TODO add auth
        startDestination = Route.Home,
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

        editorComposable<Route.Editor.Note>(navController)
        editorComposable<Route.Editor.NewNote>(navController)
        editorComposable<Route.Editor.DailyNote>(navController)

        composable<Route.Search> {
        }
    }
}

private inline fun <reified T : Route.Editor> NavGraphBuilder.editorComposable(
    navController: NavHostController,
) {
    composable<T> {
        EditorScreen(
            viewModel = koinViewModel { parametersOf(it.toRoute<T>() as Route.Editor) },
            onNavigateTo = navController::navigate,
        )
    }
}
