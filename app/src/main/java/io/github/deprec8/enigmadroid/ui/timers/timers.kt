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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.ui.components.LoadingScreen
import io.github.deprec8.enigmadroid.ui.components.NoResults
import io.github.deprec8.enigmadroid.ui.components.RemoteControlActionButton
import io.github.deprec8.enigmadroid.ui.components.insets.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.search.SearchHistory
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBar
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBarDrawerNavigationButton
import io.github.deprec8.enigmadroid.ui.timers.components.TimerSetupDialog
import io.github.deprec8.enigmadroid.ui.timers.components.TimersContent
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
    val serviceBatchSet by timersViewModel.serviceBatchSet.collectAsStateWithLifecycle()
    val searchHistory by timersViewModel.searchHistory.collectAsStateWithLifecycle()
    val loadingState by timersViewModel.loadingState.collectAsStateWithLifecycle()
    val highlightedWords by timersViewModel.highlightedWords.collectAsStateWithLifecycle()

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

    Scaffold(contentWindowInsets = contentWithDrawerWindowInsets(), topBar = {
        SearchTopAppBar(
            enabled = timerBatch.result && loadingState == LoadingState.LOADED,
            textFieldState = timersViewModel.searchFieldState,
            placeholder = stringResource(R.string.search_timers),
            content = {
                if (filteredTimers != null) {
                    TimersContent(
                        timers = filteredTimers !!,
                        paddingValues = PaddingValues(0.dp),
                        highlightedWords = highlightedWords,
                        onToggleTimerStatus = {
                            timersViewModel.toggleTimerStatus(it)
                        },
                        onEditTimer = { oldTimer, newTimer ->
                            timersViewModel.editTimer(oldTimer, newTimer)
                        },
                        onDeleteTimer = {
                            timersViewModel.deleteTimer(it)
                        },
                        serviceBatchSet = serviceBatchSet
                    )
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
                RemoteControlActionButton(onNavigateToRemoteControl = { onNavigateToRemoteControl() })
            })

    }, floatingActionButton = {
        AnimatedVisibility(
            loadingState == LoadingState.LOADED, enter = scaleIn(), exit = scaleOut()
        ) {
            Column(horizontalAlignment = Alignment.End) {
                TooltipBox(
                    tooltip = {
                        PlainTooltip {
                            Text(stringResource(id = R.string.reload))
                        }
                    },
                    state = rememberTooltipState(),
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Above, 4.dp
                    )
                ) {
                    SmallFloatingActionButton(onClick = {
                        timersViewModel.fetchData()
                    }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.reload)
                        )
                    }
                }
                TooltipBox(
                    tooltip = {
                        PlainTooltip {
                            Text(stringResource(id = R.string.add_timer))
                        }
                    },
                    state = rememberTooltipState(),
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Start, 4.dp
                    )
                ) {
                    FloatingActionButton(onClick = { showTimerSetupDialog = true }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.add_timer)
                        )
                    }
                }
            }
        }
    }

    ) { innerPadding ->
        if (timerBatch.timers.isNotEmpty() && loadingState == LoadingState.LOADED) {
            TimersContent(
                timers = timerBatch.timers,
                paddingValues = innerPadding,
                onToggleTimerStatus = {
                    timersViewModel.toggleTimerStatus(it)
                },
                onEditTimer = { oldTimer, newTimer ->
                    timersViewModel.editTimer(oldTimer, newTimer)
                },
                onDeleteTimer = {
                    timersViewModel.deleteTimer(it)
                },
                serviceBatchSet = serviceBatchSet
            )
        } else if (timerBatch.result && loadingState == LoadingState.LOADED) {
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
            serviceBatchSet = serviceBatchSet,
        )
    }
}