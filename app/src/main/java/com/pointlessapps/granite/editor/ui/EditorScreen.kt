package com.pointlessapps.granite.editor.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.pointlessapps.granite.LocalSnackbarHostState
import com.pointlessapps.granite.R
import com.pointlessapps.granite.editor.ui.components.EditorContent
import com.pointlessapps.granite.navigation.Route
import com.pointlessapps.granite.ui.components.BottomBarBackground
import com.pointlessapps.granite.ui.components.BottomBarButton
import com.pointlessapps.granite.ui.components.ComposeLoader
import com.pointlessapps.granite.ui.components.ComposeScaffoldLayout
import com.pointlessapps.granite.ui.components.TopBar
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import com.pointlessapps.granite.ui.R as RC

@Composable
internal fun EditorScreen(
    viewModel: EditorViewModel = koinViewModel(),
    onNavigateTo: (Route) -> Unit,
) {
    val localSnackbarHostState = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()

    LifecycleResumeEffect(Unit) {
        coroutineScope.launch {
            viewModel.events.collect {
                when (it) {
                    is EditorEvent.ShowSnackbar -> localSnackbarHostState.showSnackbar(it.message)
                    is EditorEvent.NavigateTo -> onNavigateTo(it.route)
                }
            }
        }

        onPauseOrDispose { viewModel.saveNote() }
    }

    ComposeScaffoldLayout(
        topBar = {
            TopBar(
                leftIcon = RC.drawable.ic_arrow_down,
                leftIconTooltip = R.string.hide,
                rightIcon = RC.drawable.ic_warning,
                rightIconTooltip = R.string.file_info,
                title = if (viewModel.isDailyNote()) R.string.daily_note else R.string.note,
                onLeftIconClicked = {},
                onRightIconClicked = {},
            )
        },
        fab = {
            // TODO Hide the navbar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                    .padding(dimensionResource(id = RC.dimen.margin_semi_big)),
                contentAlignment = Alignment.Center,
            ) {
                BottomBarBackground {
                    BottomBarButton(
                        bottomBarButton = BottomBarButton.Empty(iconRes = RC.drawable.ic_arrow_left),
                        onClicked = {}, // TODO
                        onLongClicked = {},
                    )
                    BottomBarButton(
                        bottomBarButton = BottomBarButton.Active(
                            iconRes = RC.drawable.ic_today,
                            title = "today",
                        ),
                        onClicked = {}, // TODO
                        onLongClicked = {},
                    )
                    BottomBarButton(
                        bottomBarButton = BottomBarButton.Empty(iconRes = RC.drawable.ic_arrow_right),
                        onClicked = {}, // TODO
                        onLongClicked = {},
                    )
                }
            }
        },
        content = { contentPadding ->
            EditorContent(
                contentPadding = contentPadding,
                title = viewModel.state.title,
                onTitleChanged = viewModel::onTitleChanged,
                content = viewModel.state.content,
                onContentChanged = viewModel::onContentChanged,
            )
        },
    )

    ComposeLoader(viewModel.state.isLoading)
}
