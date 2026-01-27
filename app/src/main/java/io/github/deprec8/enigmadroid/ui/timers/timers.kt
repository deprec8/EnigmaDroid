/*
 * Copyright (C) 2025-2026 deprec8
 *
 * This file is part of EnigmaDroid.
 *
 * EnigmaDroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EnigmaDroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EnigmaDroid.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.deprec8.enigmadroid.ui.timers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.TimerOff
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.data.enums.TimerState
import io.github.deprec8.enigmadroid.model.api.timers.Timer
import io.github.deprec8.enigmadroid.model.menu.MenuItem
import io.github.deprec8.enigmadroid.model.menu.MenuItemGroup
import io.github.deprec8.enigmadroid.ui.components.LoadingScreen
import io.github.deprec8.enigmadroid.ui.components.NoResults
import io.github.deprec8.enigmadroid.ui.components.content.ContentListItem
import io.github.deprec8.enigmadroid.ui.components.insets.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.search.SearchHistory
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBar
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBarDrawerNavigationButton
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBarRemoteControlActionButton
import io.github.deprec8.enigmadroid.ui.timers.components.DeleteTimerDialog
import io.github.deprec8.enigmadroid.ui.timers.components.TimerLogDialog
import io.github.deprec8.enigmadroid.ui.timers.components.TimerSetupDialog
import io.github.deprec8.enigmadroid.utils.TimestampUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimersPage(
    onNavigateToRemoteControl: () -> Unit,
    drawerState: DrawerState,
    timersViewModel: TimersViewModel = hiltViewModel()
) {

    val filteredTimers by timersViewModel.filteredTimers.collectAsStateWithLifecycle()
    val timerBatch by timersViewModel.timerBatch.collectAsStateWithLifecycle()
    val services by timersViewModel.services.collectAsStateWithLifecycle()
    val searchHistory by timersViewModel.searchHistory.collectAsStateWithLifecycle()
    val loadingState by timersViewModel.loadingState.collectAsStateWithLifecycle()
    val searchInput by timersViewModel.searchInput.collectAsStateWithLifecycle()
    val useSearchHighlighting by timersViewModel.useSearchHighlighting.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    var showTimerSetupDialog by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        timersViewModel.updateLoadingState(false)
    }

    LaunchedEffect(loadingState) {
        if (loadingState == LoadingState.LOADED) {
            timersViewModel.fetchData()
        }
    }

    @Composable
    fun getTimerState(timer: Timer): String {
        return when (timer.state + timer.disabled) {
            TimerState.WAITING.id  -> stringResource(R.string.waiting)
            TimerState.PREPARED.id -> stringResource(R.string.prepared)
            TimerState.RUNNING.id  -> stringResource(R.string.running)
            TimerState.ENDED.id    -> stringResource(R.string.ended)
            TimerState.DISABLED.id -> stringResource(R.string.disabled)
            else                   -> {
                stringResource(R.string.unknown)
            }
        }
    }

    @Composable
    fun Content(
        timers: List<Timer>,
        paddingValues: PaddingValues,
        highlightedWords: List<String> = emptyList()
    ) {
        if (timers.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(310.dp),
                contentPadding = paddingValues,
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(paddingValues)
                    .imePadding()

            ) {
                items(timers) { timer ->
                    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
                    var showEditDialog by rememberSaveable { mutableStateOf(false) }
                    var showLogDialog by rememberSaveable { mutableStateOf(false) }

                    ContentListItem(
                        highlightedWords = highlightedWords,
                        headlineText = timer.title,
                        overlineText = "${timer.serviceName} - ${getTimerState(timer)}",
                        leadingContent = {
                            when (timer.state + timer.disabled) {
                                TimerState.WAITING.id  -> Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.secondary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Timer,
                                        contentDescription = stringResource(R.string.waiting),
                                        tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                                TimerState.PREPARED.id -> Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.secondary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Checklist,
                                        contentDescription = stringResource(R.string.prepared),
                                        tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                                TimerState.RUNNING.id  -> Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Videocam,
                                        contentDescription = stringResource(R.string.running),
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                                TimerState.ENDED.id    -> Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.tertiary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Done,
                                        contentDescription = stringResource(R.string.ended),
                                        tint = MaterialTheme.colorScheme.onTertiary
                                    )
                                }
                                TimerState.DISABLED.id -> Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.tertiary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.TimerOff,
                                        contentDescription = stringResource(R.string.disabled),
                                        tint = MaterialTheme.colorScheme.onTertiary
                                    )
                                }
                                else                   -> {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                            .background(MaterialTheme.colorScheme.error),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.QuestionMark,
                                            contentDescription = stringResource(R.string.unknown),
                                            tint = MaterialTheme.colorScheme.onError
                                        )
                                    }
                                }
                            }
                        },
                        supportingText = TimestampUtils.formatApiTimestampToDate(timer.beginTimestamp) + " " + TimestampUtils.formatApiTimestampToTime(
                            timer.beginTimestamp
                        ) + " - " + TimestampUtils.formatApiTimestampToDate(timer.beginTimestamp) + " " + TimestampUtils.formatApiTimestampToTime(
                            timer.endTimestamp
                        ),
                        shortDescription = timer.shortDescription,
                        longDescription = timer.descriptionextended,
                        menuItemGroups = if (timer.logEntries.isNotEmpty()) {
                            listOf(
                                MenuItemGroup(
                                    listOf(
                                        MenuItem(
                                            text = stringResource(R.string.view_log),
                                            outlinedIcon = Icons.AutoMirrored.Outlined.List,
                                            filledIcon = Icons.AutoMirrored.Filled.List,
                                            action = { showLogDialog = true }),
                                    )
                                )
                            )
                        } else {
                            emptyList()
                        } + listOf(
                            MenuItemGroup(
                                listOf(
                                    MenuItem(
                                        text = stringResource(R.string.edit),
                                        outlinedIcon = Icons.Outlined.Edit,
                                        filledIcon = Icons.Filled.Edit,
                                        action = { showEditDialog = true }), MenuItem(
                                        text = stringResource(R.string.delete),
                                        outlinedIcon = Icons.Outlined.Delete,
                                        filledIcon = Icons.Filled.Delete,
                                        action = { showDeleteDialog = true })
                                )
                            )
                        )
                    )

                    if (showLogDialog) {
                        TimerLogDialog(timer) { showLogDialog = false }
                    }

                    if (showDeleteDialog) {
                        DeleteTimerDialog({ showDeleteDialog = false }, {
                            timersViewModel.deleteTimer(timer)
                            showDeleteDialog = false
                        })
                    }
                    if (showEditDialog) {
                        TimerSetupDialog(
                            onDismissRequest = { showEditDialog = false },
                            oldTimer = timer,
                            onSaveRequest = { newTimer, oldTimer ->
                                if (oldTimer != null) {
                                    timersViewModel.editTimer(oldTimer, newTimer)
                                }
                                showEditDialog = false
                            },
                            services = services,
                        )
                    }

                }
            }
        } else {
            NoResults(
                Modifier
                    .consumeWindowInsets(paddingValues)
                    .padding(paddingValues)
            )
        }
    }

    Scaffold(contentWindowInsets = contentWithDrawerWindowInsets(), topBar = {
        SearchTopAppBar(
            enabled = timerBatch.result,
            textFieldState = timersViewModel.searchFieldState,
            placeholder = stringResource(R.string.search_timers),
            content = {
                if (filteredTimers != null) {
                    Content(
                        timers = filteredTimers !!,
                        paddingValues = PaddingValues(0.dp),
                        highlightedWords = if (useSearchHighlighting) searchInput.split(" ")
                            .filter { it.isNotBlank() } else emptyList())
                } else {
                    SearchHistory(searchHistory = searchHistory, onTermSearchClick = {
                        timersViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(it)
                        timersViewModel.updateSearchInput()
                    }, onTermInsertClick = {
                        timersViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(
                            it
                        )
                    })
                }
            },
            navigationButton = { searchBarState ->
                SearchTopAppBarDrawerNavigationButton(drawerState, searchBarState)
            },
            onSearch = {
                timersViewModel.updateSearchInput()
            },
            actionButtons = {
                SearchTopAppBarRemoteControlActionButton(onNavigateToRemoteControl = { onNavigateToRemoteControl() })
            })

    }, floatingActionButton = {
        AnimatedVisibility(
            loadingState == LoadingState.LOADED, enter = scaleIn(), exit = scaleOut()
        ) {
            Column {
                SmallFloatingActionButton(onClick = {
                    timersViewModel.fetchData()
                }, Modifier.align(Alignment.End)) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.refresh_page)
                    )
                }
                FloatingActionButton(onClick = { showTimerSetupDialog = true }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_timer)
                    )
                }
            }
        }
    }

    ) { innerPadding ->
        if (timerBatch.timers.isNotEmpty()) {
            Content(
                timers = timerBatch.timers, innerPadding
            )
        } else if (timerBatch.result) {
            NoResults(
                Modifier
                    .consumeWindowInsets(innerPadding)
                    .padding(innerPadding)
            )
        } else {
            LoadingScreen(
                Modifier
                    .consumeWindowInsets(innerPadding)
                    .padding(innerPadding),
                onUpdateLoadingState = {
                    scope.launch {
                        timersViewModel.updateLoadingState(
                            it
                        )
                    }
                },
                loadingState = loadingState
            )
        }
    }

    if (showTimerSetupDialog) {
        TimerSetupDialog(
            onDismissRequest = { showTimerSetupDialog = false },
            onSaveRequest = { newTimer, _ ->
                timersViewModel.addTimer(newTimer)
                showTimerSetupDialog = false
            },
            services = services,
        )
    }
}