package com.pointlessapps.granite.editor.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.pointlessapps.granite.LocalSnackbarHostState
import com.pointlessapps.granite.R
import com.pointlessapps.granite.editor.ui.components.EditorContent
import com.pointlessapps.granite.editor.ui.components.bottomsheet.ConsoleOutputBottomSheet
import com.pointlessapps.granite.navigation.Route
import com.pointlessapps.granite.relative.datetime.formatter.RelativeDatetimeFormatter
import com.pointlessapps.granite.relative.datetime.formatter.RelativeDatetimeFormatter.Result.Absolute
import com.pointlessapps.granite.relative.datetime.formatter.RelativeDatetimeFormatter.Result.DaysAgo
import com.pointlessapps.granite.relative.datetime.formatter.RelativeDatetimeFormatter.Result.HoursAgo
import com.pointlessapps.granite.relative.datetime.formatter.RelativeDatetimeFormatter.Result.LastWeek
import com.pointlessapps.granite.relative.datetime.formatter.RelativeDatetimeFormatter.Result.LessThanMinuteAgo
import com.pointlessapps.granite.relative.datetime.formatter.RelativeDatetimeFormatter.Result.MinutesAgo
import com.pointlessapps.granite.relative.datetime.formatter.RelativeDatetimeFormatter.Result.Yesterday
import com.pointlessapps.granite.ui.components.BottomBarBackground
import com.pointlessapps.granite.ui.components.BottomBarButton
import com.pointlessapps.granite.ui.components.ComposeLoader
import com.pointlessapps.granite.ui.components.ComposeScaffoldLayout
import com.pointlessapps.granite.ui.components.TopBar
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Date
import com.pointlessapps.granite.ui.R as RC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditorScreen(
    viewModel: EditorViewModel = koinViewModel(),
    onNavigateTo: (Route) -> Unit,
) {
    val localSnackbarHostState = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()
    var showConsole by remember { mutableStateOf<Boolean?>(null) }
    val consoleBottomSheetState = rememberModalBottomSheetState()

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
                title = if (viewModel.state.isDailyNote) R.string.daily_note else R.string.note,
                onLeftIconClicked = {},
                onRightIconClicked = {},
            )
        },
        fab = {
            if (viewModel.state.isDailyNote) {
                val dateLiteral = when (
                    val result = RelativeDatetimeFormatter.formatDateTime(viewModel.state.createdAt)
                ) {
                    LessThanMinuteAgo, is MinutesAgo, is HoursAgo -> stringResource(R.string.date_today)
                    Yesterday -> stringResource(R.string.date_yesterday)
                    is DaysAgo -> stringResource(R.string.date_days_ago, result.days)
                    LastWeek -> stringResource(R.string.date_last_week)
                    is Absolute -> stringResource(
                        R.string.date_absolute,
                        Date(result.time),
                    )
                }

                DailyNoteBottomBar(
                    dateLiteral = if (viewModel.state.createdAt != -1L) dateLiteral else "",
                    onPreviousClicked = {},
                    onNextClicked = {},
                    isPreviousEnabled = false,
                    isNextEnabled = false,
                )
            }
        },
        content = { contentPadding ->
            EditorContent(
                contentPadding = contentPadding,
                title = viewModel.state.title,
                onTitleChanged = viewModel::onTitleChanged,
                properties = viewModel.state.properties,
                content = viewModel.state.content,
                onContentChanged = viewModel::onContentChanged,
                readOnlyTitle = viewModel.state.isDailyNote,
                onRunCodeBlock = viewModel::onRunCodeBlock,
            )
        },
    )

    ComposeLoader(viewModel.state.isLoading)

    showConsole?.let { isLoading ->
        ConsoleOutputBottomSheet(
            state = consoleBottomSheetState,
            isLoading = isLoading,
            acceptsInput = viewModel.consoleAcceptsInput,
            output = viewModel.consoleOutput,
            onInputCallback = viewModel::onConsoleInput,
            onDismissRequest = {
                viewModel.cancelMicaProcess()
                coroutineScope.launch {
                    consoleBottomSheetState.hide()
                }.invokeOnCompletion { showConsole = null }
            },
        )
    }
}

@Composable
private fun DailyNoteBottomBar(
    dateLiteral: String,
    onPreviousClicked: () -> Unit,
    onNextClicked: () -> Unit,
    isPreviousEnabled: Boolean,
    isNextEnabled: Boolean,
) {
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
                isEnabled = isPreviousEnabled,
                onClicked = onPreviousClicked,
                onLongClicked = {},
            )
            BottomBarButton(
                bottomBarButton = BottomBarButton.Active(
                    iconRes = RC.drawable.ic_today,
                    title = dateLiteral,
                ),
                isEnabled = true,
                onClicked = {}, // TODO
                onLongClicked = {},
            )
            BottomBarButton(
                bottomBarButton = BottomBarButton.Empty(iconRes = RC.drawable.ic_arrow_right),
                isEnabled = isNextEnabled,
                onClicked = onNextClicked,
                onLongClicked = {},
            )
        }
    }
}
