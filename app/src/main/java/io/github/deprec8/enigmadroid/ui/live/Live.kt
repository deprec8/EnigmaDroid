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

package io.github.deprec8.enigmadroid.ui.live

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.ui.components.FloatingReloadButton
import io.github.deprec8.enigmadroid.ui.components.InvalidResponse
import io.github.deprec8.enigmadroid.ui.components.Loading
import io.github.deprec8.enigmadroid.ui.components.ObserveActiveState
import io.github.deprec8.enigmadroid.ui.components.content.ContentTab
import io.github.deprec8.enigmadroid.ui.components.content.ContentTabRow
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.navigation.RemoteControlActionButton
import io.github.deprec8.enigmadroid.ui.components.search.SearchHistory
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBar
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBarDrawerNavigationButton
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.collections.forEachIndexed
import kotlin.collections.getOrNull
import kotlin.collections.isNotEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LivePage(
    contentType: ContentType,
    onNavigateToRemoteControl: () -> Unit,
    onNavigateToServiceEpg: (String, String) -> Unit,
    drawerState: DrawerState,
    liveViewModel: LiveViewModel = koinViewModel(parameters = { parametersOf(contentType) })
) {

    val filteredEvents by liveViewModel.filteredEvents.collectAsStateWithLifecycle()
    val eventBatchesResult by liveViewModel.eventBatchesResult.collectAsStateWithLifecycle()
    val searchHistory by liveViewModel.searchHistory.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { eventBatchesResult?.getOrNull()?.size ?: 0 })
    val selectedTabIndex by remember {
        derivedStateOf {
            pagerState.currentPage.coerceIn(
                0, ((eventBatchesResult?.getOrNull()?.size ?: 0) - 1).coerceAtLeast(0)
            )
        }
    }

    ObserveActiveState(liveViewModel)

    LaunchedEffect(selectedTabIndex) {
        liveViewModel.updateCurrentBouquetIndex(selectedTabIndex)
    }

    Scaffold(floatingActionButton = {
        FloatingReloadButton(eventBatchesResult?.isSuccess == true) { liveViewModel.fetchData() }
    }, contentWindowInsets = contentWithDrawerWindowInsets(), topBar = {
        SearchTopAppBar(
            textFieldState = liveViewModel.searchFieldState,
            enabled = eventBatchesResult?.getOrNull()?.isNotEmpty() == true,
            placeholder = stringResource(R.string.search_events),
            content = {
                filteredEvents?.let { filterEvents ->
                    LiveContent(
                        events = filterEvents,
                        paddingValues = PaddingValues(0.dp),
                        showChannelNumbers = false,
                        onNavigateToServiceEpg = { serviceReference, serviceName ->
                            onNavigateToServiceEpg(
                                serviceReference, serviceName
                            )
                        },
                        onPlayOnDevice = {
                            liveViewModel.playOnDevice(it)
                        },
                        onAddTimerForEvent = {
                            liveViewModel.addTimerForEvent(it)
                        },
                        buildLiveStreamUri = {
                            liveViewModel.buildLiveStreamUri(it)
                        })
                } ?: run {
                    SearchHistory(searchHistory = searchHistory, onSearchQuery = {
                        liveViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(it)
                        liveViewModel.updateSearchInput()
                    }, onInsertQuery = {
                        liveViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(it)
                    }, onRemoveItem = {
                        liveViewModel.deleteFromSearchHistory(it)
                    })
                }
            },
            navigationButton = { searchBarState ->
                SearchTopAppBarDrawerNavigationButton(drawerState, searchBarState)
            },
            actionButtons = {
                RemoteControlActionButton(onNavigateToRemoteControl = { onNavigateToRemoteControl() })
            },
            onSearch = {
                liveViewModel.updateSearchInput()
            },
            actionBar = {
                eventBatchesResult?.onSuccess { eventBatches ->
                    if (eventBatches.isNotEmpty()) {
                        ContentTabRow(selectedTabIndex) {
                            eventBatches.forEachIndexed { index, eventBatch ->
                                ContentTab(
                                    name = eventBatch.name, selected = index == selectedTabIndex
                                ) {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            }
                        }
                    }
                }
            })

    }) { innerPadding ->
        if (eventBatchesResult != null) {
            eventBatchesResult?.onSuccess { eventBatches ->
                HorizontalPager(
                    modifier = Modifier.fillMaxSize(), state = pagerState
                ) { index ->
                    LiveContent(
                        events = eventBatches.getOrNull(index)?.events ?: emptyList(),
                        paddingValues = innerPadding,
                        onNavigateToServiceEpg = { serviceReference, serviceName ->
                            onNavigateToServiceEpg(
                                serviceReference, serviceName
                            )
                        },
                        onPlayOnDevice = {
                            liveViewModel.playOnDevice(it)
                        },
                        onAddTimerForEvent = {
                            liveViewModel.addTimerForEvent(it)
                        },
                        buildLiveStreamUri = {
                            liveViewModel.buildLiveStreamUri(it)
                        })
                }
            }?.onFailure { e ->
                InvalidResponse(
                    throwable = e, modifier = Modifier
                        .consumeWindowInsets(innerPadding)
                        .padding(
                            innerPadding
                        )
                ) {
                    liveViewModel.fetchData(true)
                }
            }
        } else {
            Loading(
                Modifier
                    .consumeWindowInsets(innerPadding)
                    .padding(
                        innerPadding
                    )
            )
        }
    }
}