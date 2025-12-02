/*
 * Copyright (C) 2025 deprec8
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.TimerOff
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.data.objects.TimerState
import io.github.deprec8.enigmadroid.model.api.Timer
import io.github.deprec8.enigmadroid.model.menu.MenuItem
import io.github.deprec8.enigmadroid.model.menu.MenuSection
import io.github.deprec8.enigmadroid.ui.components.ContentListItem
import io.github.deprec8.enigmadroid.ui.components.LoadingScreen
import io.github.deprec8.enigmadroid.ui.components.NoResults
import io.github.deprec8.enigmadroid.ui.components.SearchHistory
import io.github.deprec8.enigmadroid.ui.components.SearchTopAppBar
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.utils.TimestampUtils
import kotlinx.coroutines.launch

@Composable
fun TimersPage(
    onNavigateToRemoteControl: () -> Unit,
    drawerState: DrawerState,
    timersViewModel: TimersViewModel = hiltViewModel()
) {

    val filteredTimers by timersViewModel.filteredTimers.collectAsStateWithLifecycle()
    val timerList by timersViewModel.timerList.collectAsStateWithLifecycle()
    val services by timersViewModel.services.collectAsStateWithLifecycle()
    val searchHistory by timersViewModel.searchHistory.collectAsStateWithLifecycle()
    var showTimerSetupDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val loadingState by timersViewModel.loadingState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val searchInput by timersViewModel.searchInput.collectAsStateWithLifecycle()
    val useSearchHighlighting by timersViewModel.useSearchHighlighting.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        timersViewModel.updateLoadingState(false)
    }

    LaunchedEffect(loadingState) {
        if (loadingState == LoadingState.LOADED) {
            timersViewModel.fetchData()
        }
    }

    @Composable
    fun Content(
        list: List<Timer>,
        paddingValues: PaddingValues,
        highlightedWords: List<String> = emptyList()
    ) {
        if (list.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(310.dp),
                contentPadding = paddingValues,
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(paddingValues)
                    .imePadding()

            ) {
                items(list) { timer ->
                    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
                    var showEditDialog by rememberSaveable { mutableStateOf(false) }

                    ContentListItem(
                        highlightedWords = highlightedWords,
                        headlineText = timer.title,
                        overlineText = timer.serviceName,
                        leadingContent = {
                            when (timer.state + timer.disabled) {
                                TimerState.WAITING -> Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.Yellow),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Timer,
                                        contentDescription = "Waiting",
                                        tint = Color.Black
                                    )
                                }
                                TimerState.PREPARED -> Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(255, 165, 0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Checklist,
                                        contentDescription = "Prepared",
                                        tint = Color.Black
                                    )
                                }
                                TimerState.RUNNING -> Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.Green),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Videocam,
                                        contentDescription = "Running",
                                        tint = Color.Black
                                    )
                                }
                                TimerState.ENDED -> Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.Blue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Done,
                                        contentDescription = "Prepared",
                                        tint = Color.Black
                                    )
                                }
                                TimerState.DISABLED -> Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.TimerOff,
                                        contentDescription = "Prepared",
                                        tint = Color.Black
                                    )
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
                        menuSections = listOf(
                            MenuSection(
                                listOf(
                                    MenuItem(
                                        text = stringResource(R.string.edit),
                                        outlinedIcon = Icons.Outlined.Edit,
                                        filledIcon = Icons.Filled.Edit,
                                        action = { showEditDialog = true }),
                                    MenuItem(
                                        text = stringResource(R.string.delete),
                                        outlinedIcon = Icons.Outlined.Delete,
                                        filledIcon = Icons.Filled.Delete,
                                        action = { showDeleteDialog = true })
                                )
                            )
                        )
                    )

                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showDeleteDialog = false
                            },
                            title = { Text(text = stringResource(R.string.delete_timer)) },
                            text = { Text(text = stringResource(R.string.if_you_delete_this_timer_it_will_not_be_recoverable)) },
                            icon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
                            confirmButton = {
                                TextButton(onClick = {
                                    timersViewModel.deleteTimer(timer)
                                    showDeleteDialog = false
                                }) { Text(stringResource(R.string.confirm)) }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showDeleteDialog = false
                                }) { Text(stringResource(R.string.cancel)) }
                            }
                        )
                    }
                    if (showEditDialog) {
                        TimerSetupDialog(
                            onDismiss = { showEditDialog = false },
                            oldTimer = timer,
                            onSave = { newTimer, oldTimer ->
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

    Scaffold(
        contentWindowInsets = contentWithDrawerWindowInsets(),
        topBar = {
            SearchTopAppBar(
                enabled = timerList.result,
                textFieldState = timersViewModel.searchFieldState,
                placeholder = stringResource(R.string.search_timers),
                content = {
                    if (filteredTimers != null) {
                        Content(
                            list = filteredTimers !!,
                            paddingValues = PaddingValues(0.dp),
                            highlightedWords = if (useSearchHighlighting) searchInput.split(" ")
                                .filter { it.isNotBlank() } else emptyList()
                        )
                    } else {
                        SearchHistory(
                            searchHistory = searchHistory,
                            onTermSearchClick = {
                                timersViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(it)
                                timersViewModel.updateSearchInput()
                            },
                            onTermInsertClick = {
                                timersViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(
                                    it
                                )
                            }
                        )
                    }
                },
                drawerState = drawerState,
                onSearch = {
                    timersViewModel.updateSearchInput()
                },
                onNavigateToRemote = { onNavigateToRemoteControl() }
            )

        }, floatingActionButton = {
            AnimatedVisibility(
                loadingState == LoadingState.LOADED,
                enter = scaleIn(),
                exit = scaleOut()
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
        if (timerList.timers.isNotEmpty()) {
            Content(
                list = timerList.timers,
                innerPadding
            )
        } else if (timerList.result) {
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
                updateLoadingState = {
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
            onDismiss = { showTimerSetupDialog = false },
            onSave = { newTimer, _ ->
                timersViewModel.addTimer(newTimer)
                showTimerSetupDialog = false
            },
            services = services,
        )

    }
}