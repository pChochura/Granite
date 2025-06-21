package com.pointlessapps.granite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.dimensionResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.pointlessapps.granite.navigation.Navigation
import com.pointlessapps.granite.ui.components.ComposeSnackbar
import com.pointlessapps.granite.ui.components.ComposeSnackbarHostState
import com.pointlessapps.granite.ui.components.rememberComposeSnackbarHostState
import com.pointlessapps.granite.ui.components.theme.ProjectTheme
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
                val composeSnackbarHostState = rememberComposeSnackbarHostState(snackbarHostState)

                CompositionLocalProvider(
                    LocalSnackbarHostState provides composeSnackbarHostState,
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

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .systemBarsPadding()
                                .imePadding()
                                .padding(dimensionResource(RC.dimen.margin_semi_big)),
                            contentAlignment = Alignment.BottomCenter,
                        ) {
                            SnackbarHost(hostState = snackbarHostState) {
                                ComposeSnackbar(
                                    message = it.visuals.message,
                                    actionLabel = it.visuals.actionLabel,
                                    actionCallback = { it.performAction() },
                                    onDismissRequest = { it.dismiss() },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

internal val LocalSnackbarHostState = compositionLocalOf<ComposeSnackbarHostState> {
    error("No ComposeSnackbarHostState found")
}
