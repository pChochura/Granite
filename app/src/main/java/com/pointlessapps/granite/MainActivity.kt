package com.pointlessapps.granite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.dimensionResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pointlessapps.granite.home.ui.components.menu.bottomsheet.DailyNoteButtonBottomSheet
import com.pointlessapps.granite.navigation.Navigation
import com.pointlessapps.granite.navigation.Route
import com.pointlessapps.granite.ui.components.BottomBar
import com.pointlessapps.granite.ui.components.BottomSectionState
import com.pointlessapps.granite.ui.components.ComposeSnackbar
import com.pointlessapps.granite.ui.components.ComposeSnackbarHostState
import com.pointlessapps.granite.ui.components.rememberComposeSnackbarHostState
import com.pointlessapps.granite.ui.components.theme.ProjectTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import com.pointlessapps.granite.ui.R as RC

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { !mainViewModel.isInitialized }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.Transparent.toArgb(),
                Color.Transparent.toArgb(),
            ),
        )
        super.onCreate(savedInstanceState)

        setContent {
            ProjectTheme {
                val navigationController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val bottomSectionState = remember { BottomSectionState() }
                val composeSnackbarHostState = rememberComposeSnackbarHostState(snackbarHostState)

                CompositionLocalProvider(
                    LocalSnackbarHostState provides composeSnackbarHostState,
                    LocalBottomSectionState provides bottomSectionState,
                    LocalTextSelectionColors provides TextSelectionColors(
                        handleColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    ),
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surface,
                    ) {
                        Navigation(navigationController)
                        BottomBarSection(
                            navigationController = navigationController,
                            bottomSectionState = bottomSectionState,
                            snackbarHostState = snackbarHostState,
                            onNavigateTo = navigationController::navigate,
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
    @Composable
    private fun BottomBarSection(
        navigationController: NavHostController,
        bottomSectionState: BottomSectionState,
        snackbarHostState: SnackbarHostState,
        onNavigateTo: (Route) -> Unit,
    ) {
        val navBackStackEntry by navigationController.currentBackStackEntryAsState()

        val coroutineScope = rememberCoroutineScope()
        var showDailyNoteButtonBottomSheet by remember { mutableStateOf(false) }
        val dailyNoteButtonBottomSheetState = rememberModalBottomSheetState()

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            val isImeVisible = WindowInsets.isImeVisible

            AnimatedContent(
                targetState = !isImeVisible && Route.routesWithBottomBar.any {
                    navBackStackEntry?.destination?.hasRoute(it) == true
                },
                transitionSpec = {
                    (fadeIn() + slideInVertically(initialOffsetY = { it / 2 }))
                        .togetherWith(fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }))
                },
                label = "bottomFabBar visibility",
            ) { isBottomBarVisible ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { bottomSectionState.currentHeight.intValue = it.height }
                        .wrapContentSize()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(dimensionResource(id = RC.dimen.margin_semi_big)),
                    verticalArrangement = Arrangement.spacedBy(
                        space = dimensionResource(RC.dimen.margin_medium),
                        alignment = Alignment.Bottom,
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    SnackbarHost(hostState = snackbarHostState) {
                        ComposeSnackbar(
                            message = it.visuals.message,
                            actionLabel = it.visuals.actionLabel,
                            actionCallback = it::performAction,
                            onDismissRequest = it::dismiss,
                        )
                    }

                    if (isBottomBarVisible) {
                        BottomBar(
                            currentDestination = navBackStackEntry?.destination,
                            onNavigateTo = {
                                if (navBackStackEntry?.destination?.hasRoute(it::class) != true) {
                                    onNavigateTo(it)
                                }
                            },
                            onLongClicked = {
                                if (it == Route.DailyNote) {
                                    showDailyNoteButtonBottomSheet = true
                                    coroutineScope.launch {
                                        dailyNoteButtonBottomSheetState.show()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        if (showDailyNoteButtonBottomSheet) {
            DailyNoteButtonBottomSheet(
                state = dailyNoteButtonBottomSheetState,
                onHideButtonClicked = {},
                onDisableClicked = {},
                onDismissRequest = {
                    coroutineScope.launch {
                        dailyNoteButtonBottomSheetState.hide()
                    }.invokeOnCompletion {
                        showDailyNoteButtonBottomSheet = false
                    }
                },
            )
        }
    }
}

internal val LocalSnackbarHostState = compositionLocalOf<ComposeSnackbarHostState> {
    error("No ComposeSnackbarHostState found")
}

internal val LocalBottomSectionState = compositionLocalOf<BottomSectionState> {
    error("No BottomSectionState found")
}