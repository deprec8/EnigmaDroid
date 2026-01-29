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

package io.github.deprec8.enigmadroid.ui.live.radio

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.ui.components.FloatingRefreshButton
import io.github.deprec8.enigmadroid.ui.components.LoadingScreen
import io.github.deprec8.enigmadroid.ui.components.RemoteControlActionButton
import io.github.deprec8.enigmadroid.ui.components.insets.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.search.SearchHistory
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBar
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBarDrawerNavigationButton
import io.github.deprec8.enigmadroid.ui.live.components.LiveContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioPage(
    onNavigateToRemoteControl: () -> Unit,
    onNavigateToServiceEpg: (serviceReference: String, serviceName: String) -> Unit,
    drawerState: DrawerState,
    radioViewModel: RadioViewModel = hiltViewModel()
) {

    val filteredEvents by radioViewModel.filteredEvents.collectAsStateWithLifecycle()
    val eventBatches by radioViewModel.eventBatches.collectAsStateWithLifecycle()
    val searchHistory by radioViewModel.searchHistory.collectAsStateWithLifecycle()
    val useSearchHighlighting by radioViewModel.useSearchHighlighting.collectAsStateWithLifecycle()
    val loadingState by radioViewModel.loadingState.collectAsStateWithLifecycle()
    val searchInput by radioViewModel.searchInput.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { eventBatches.size })
    val selectedTabIndex = remember {
        derivedStateOf {
            pagerState.currentPage.coerceIn(
                0, (if (eventBatches.size - 1 < 0) {
                    0
                } else {
                    eventBatches.size - 1
                })
            )
        }
    }

    LaunchedEffect(Unit) {
        radioViewModel.updateLoadingState(false)
    }

    LaunchedEffect(loadingState) {
        if (loadingState == LoadingState.LOADED) {
            radioViewModel.fetchData()
        }
    }

    Scaffold(floatingActionButton = {
        FloatingRefreshButton(loadingState) { radioViewModel.fetchData() }
    }, contentWindowInsets = contentWithDrawerWindowInsets(), topBar = {
        SearchTopAppBar(
            enabled = eventBatches.isNotEmpty(),
            textFieldState = radioViewModel.searchFieldState,
            placeholder = stringResource(R.string.search_events),
            content = {
                if (filteredEvents != null) {
                    LiveContent(
                        events = filteredEvents !!,
                        paddingValues = PaddingValues(0.dp),
                        showChannelNumbers = false,
                        highlightedWords = if (useSearchHighlighting) searchInput.split(" ")
                            .filter { it.isNotBlank() } else emptyList(),
                        onNavigateToServiceEpg = { serviceReference, serviceName ->
                            onNavigateToServiceEpg(
                                serviceReference, serviceName
                            )
                        },
                        onPlayOnDevice = {
                            radioViewModel.playOnDevice(it)
                        },
                        onAddTimerForEvent = {
                            radioViewModel.addTimerForEvent(it)
                        },
                        buildLiveStreamUrl = {
                            radioViewModel.buildLiveStreamUrl(it)
                        })
                } else {
                    SearchHistory(searchHistory = searchHistory, onTermSearchClick = {
                        radioViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(it)
                        radioViewModel.updateSearchInput(selectedTabIndex.value)
                    }, onTermInsertClick = {
                        radioViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(
                            it
                        )
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
                radioViewModel.updateSearchInput(selectedTabIndex.value)
            },
            tabBar = {
                if (eventBatches.isNotEmpty()) {
                    PrimaryScrollableTabRow(
                        selectedTabIndex = selectedTabIndex.value,
                        divider = { },
                        scrollState = rememberScrollState()
                    ) {
                        eventBatches.forEachIndexed { index, eventBatch ->
                            Tab(
                                text = {
                                    Text(
                                        text = eventBatch.name,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                selected = index == selectedTabIndex.value,
                            )
                        }
                    }
                    HorizontalDivider()
                }
            })

    }

    ) { innerPadding ->
        if (eventBatches.isNotEmpty()) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) { index ->
                LiveContent(
                    events = eventBatches[index].events,
                    paddingValues = innerPadding,
                    onNavigateToServiceEpg = { serviceReference, serviceName ->
                        onNavigateToServiceEpg(
                            serviceReference, serviceName
                        )
                    },
                    onPlayOnDevice = {
                        radioViewModel.playOnDevice(it)
                    },
                    onAddTimerForEvent = {
                        radioViewModel.addTimerForEvent(it)
                    },
                    buildLiveStreamUrl = {
                        radioViewModel.buildLiveStreamUrl(it)
                    })
            }
        } else {
            LoadingScreen(
                Modifier
                    .consumeWindowInsets(innerPadding)
                    .padding(innerPadding),
                onUpdateLoadingState = {
                    scope.launch {
                        radioViewModel.updateLoadingState(
                            it
                        )
                    }
                },
                loadingState = loadingState
            )
        }
    }

}