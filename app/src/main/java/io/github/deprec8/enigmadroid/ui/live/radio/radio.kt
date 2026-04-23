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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.ui.components.ContentTab
import io.github.deprec8.enigmadroid.ui.components.ContentTabRow
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.loading.FloatingReloadButton
import io.github.deprec8.enigmadroid.ui.components.loading.LoadingScreen
import io.github.deprec8.enigmadroid.ui.components.navigation.RemoteControlActionButton
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
    val loadingState by radioViewModel.loadingState.collectAsStateWithLifecycle()
    val highlightedWords by radioViewModel.highlightedWords.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { eventBatches?.size ?: 0 })
    val selectedTabIndex by remember {
        derivedStateOf {
            pagerState.currentPage.coerceIn(0, ((eventBatches?.size ?: 0) - 1).coerceAtLeast(0))
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

    LaunchedEffect(selectedTabIndex) {
        radioViewModel.updateCurrentBouquetIndex(selectedTabIndex)
    }

    Scaffold(floatingActionButton = {
        FloatingReloadButton(loadingState) { radioViewModel.fetchData() }
    }, contentWindowInsets = contentWithDrawerWindowInsets(), topBar = {
        SearchTopAppBar(
            enabled = eventBatches?.isNotEmpty() == true && loadingState == LoadingState.LOADED,
            textFieldState = radioViewModel.searchFieldState,
            placeholder = stringResource(R.string.search_events),
            content = {
                filteredEvents?.let { filterEvents ->
                    LiveContent(
                        events = filterEvents,
                        paddingValues = PaddingValues(0.dp),
                        showChannelNumbers = false,
                        highlightedWords = highlightedWords,
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
                } ?: run {
                    SearchHistory(searchHistory = searchHistory, onTermSearchClick = {
                        radioViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(it)
                        radioViewModel.updateSearchInput()
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
                radioViewModel.updateSearchInput()
            },
            actionBar = {
                if (eventBatches?.isNotEmpty() == true && loadingState == LoadingState.LOADED) {
                    ContentTabRow(selectedTabIndex) {
                        eventBatches?.forEachIndexed { index, eventBatch ->
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
            })

    }

    ) { innerPadding ->
        if (eventBatches != null && loadingState == LoadingState.LOADED) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) { index ->
                LiveContent(
                    events = eventBatches?.get(index)?.events ?: emptyList(),
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