package com.pointlessapps.granite.home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.pointlessapps.granite.R
import com.pointlessapps.granite.home.model.Folder
import com.pointlessapps.granite.home.model.Item
import com.pointlessapps.granite.navigation.Route
import com.pointlessapps.granite.ui_components.components.ComposeIcon
import com.pointlessapps.granite.ui_components.components.ComposeIconButton
import com.pointlessapps.granite.ui_components.components.ComposeLoader
import com.pointlessapps.granite.ui_components.components.ComposeMarkdownTextField
import com.pointlessapps.granite.ui_components.components.ComposeScaffoldLayout
import com.pointlessapps.granite.ui_components.components.ComposeText
import com.pointlessapps.granite.ui_components.components.ComposeTextField
import com.pointlessapps.granite.ui_components.components.defaultComposeIconButtonStyle
import com.pointlessapps.granite.ui_components.components.defaultComposeTextFieldStyle
import com.pointlessapps.granite.ui_components.components.defaultComposeTextStyle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt
import com.pointlessapps.granite.ui_components.R as RC

@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateTo: (Route) -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val focusManager = LocalFocusManager.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.RESUMED) {
            viewModel.events.collect {
                when (it) {
                    is HomeEvent.CloseDrawer -> drawerState.close()
                }
            }
        }
    }

    LaunchedEffect(drawerState.isOpen) {
        focusManager.clearFocus()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerState) {
                DrawerMenu(
                    selectedItemId = viewModel.state.selectedItemId,
                    items = viewModel.getFlattenItems(),
                    onItemSelected = viewModel::onItemSelected,
                )
            }
        },
        content = {
            ComposeScaffoldLayout(
                modifier = Modifier.offset {
                    IntOffset(
                        x = DrawerDefaults.MaximumDrawerWidth.roundToPx() + when {
                            !drawerState.currentOffset.isNaN() -> drawerState.currentOffset.roundToInt()
                            else -> 0
                        },
                        y = 0,
                    )
                },
                topBar = {
                    TopBar(onMenuClicked = { coroutineScope.launch { drawerState.open() } })
                },
                fab = { BottomBar({}) },
            ) {
                ComposeMarkdownTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = dimensionResource(RC.dimen.margin_semi_big),
                            vertical = dimensionResource(RC.dimen.margin_tiny),
                        )
                        .padding(it),
                    value = viewModel.state.textValue,
                    onValueChange = viewModel::onTextValueChanged,
                    textFieldStyle = defaultComposeTextFieldStyle(),
                )
            }

            ComposeLoader(viewModel.state.isLoading)
        }
    )
}

@Composable
private fun TopBar(onMenuClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            .statusBarsPadding()
            .padding(all = dimensionResource(RC.dimen.margin_medium)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_tiny)),
    ) {
        ComposeIconButton(
            iconRes = RC.drawable.ic_move_handle,
            onClick = onMenuClicked,
            iconButtonStyle = defaultComposeIconButtonStyle().copy(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                outlineColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
        ComposeText(
            text = stringResource(R.string.title_note),
            modifier = Modifier.weight(1f),
            textStyle = defaultComposeTextStyle().copy(
                textColor = MaterialTheme.colorScheme.onSurface,
                typography = MaterialTheme.typography.titleLarge,
            ),
        )

        ComposeIconButton(
            iconRes = RC.drawable.ic_warning,
            onClick = {},
            iconButtonStyle = defaultComposeIconButtonStyle().copy(
                containerColor = Color.Transparent,
                outlineColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
    }
}

@Composable
private fun DrawerMenu(selectedItemId: Int, items: List<Item>, onItemSelected: (Item) -> Unit) {
    var searchValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(start = dimensionResource(RC.dimen.margin_tiny))
            .padding(all = dimensionResource(RC.dimen.margin_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_medium)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_tiny)),
        ) {
            Image(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.logo_icon_size))
                    .background(colorResource(R.color.ic_launcher_background), shape = CircleShape)
                    .padding(dimensionResource(RC.dimen.margin_tiny)),
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
            )
            ComposeText(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.app_name),
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    typography = MaterialTheme.typography.titleLarge,
                ),
            )

            ComposeIconButton(
                iconRes = RC.drawable.ic_settings,
                onClick = {},
                iconButtonStyle = defaultComposeIconButtonStyle().copy(
                    containerColor = Color.Transparent,
                    outlineColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(
                    start = dimensionResource(RC.dimen.margin_medium),
                    end = dimensionResource(RC.dimen.margin_tiny),
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
        ) {
            ComposeIcon(
                modifier = Modifier.padding(vertical = dimensionResource(RC.dimen.margin_small)),
                iconRes = RC.drawable.ic_search,
            )
            ComposeTextField(
                modifier = Modifier.weight(1f),
                value = searchValue,
                onValueChange = { searchValue = it },
                textFieldStyle = defaultComposeTextFieldStyle().copy(
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrectEnabled = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search,
                        showKeyboardOnFocus = true,
                    ),
                    placeholder = stringResource(R.string.search),
                    maxLines = 1,
                )
            )
            AnimatedVisibility(searchValue.isNotEmpty()) {
                ComposeIconButton(
                    iconRes = RC.drawable.ic_close,
                    onClick = { searchValue = "" },
                    iconButtonStyle = defaultComposeIconButtonStyle().copy(
                        containerColor = Color.Transparent,
                        outlineColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
        ) {
            items(items, key = { it.id }) { item ->
                val isSelected = item.id == selectedItemId
                Row(
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .then(
                            if (isSelected) {
                                Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                            } else {
                                Modifier
                            },
                        )
                        .clickable(onClick = { onItemSelected(item) })
                        .padding(
                            vertical = dimensionResource(RC.dimen.margin_tiny),
                            horizontal = dimensionResource(RC.dimen.margin_nano),
                        )
                        .padding(
                            start = dimensionResource(RC.dimen.margin_medium).times(item.indent),
                        ),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
                ) {
                    if (item is Folder) {
                        val rotation by animateFloatAsState(if (item.opened) 90f else 0f)
                        ComposeIcon(
                            modifier = Modifier
                                .size(dimensionResource(R.dimen.folder_icon_size))
                                .rotate(rotation),
                            iconRes = RC.drawable.ic_arrow_right,
                        )
                    }

                    ComposeText(
                        text = item.name,
                        textStyle = defaultComposeTextStyle().copy(
                            textColor = if (isSelected) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            typography = MaterialTheme.typography.bodySmall,
                        ),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BottomBar(insertStyle: () -> Unit) {
    AnimatedVisibility(
        visible = WindowInsets.isImeVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(
                        topStart = dimensionResource(RC.dimen.medium_rounded_corners),
                        topEnd = dimensionResource(RC.dimen.medium_rounded_corners),
                    ),
                )
                .navigationBarsPadding()
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_tiny)),
            contentPadding = PaddingValues(
                horizontal = dimensionResource(RC.dimen.margin_tiny),
                vertical = dimensionResource(RC.dimen.margin_nano),
            ),
        ) {
            item {
                ComposeIconButton(
                    iconRes = R.drawable.icon_bold,
                    onClick = { },
                    iconButtonStyle = defaultComposeIconButtonStyle().copy(
                        containerColor = Color.Transparent,
                        outlineColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            }
        }
    }
}
