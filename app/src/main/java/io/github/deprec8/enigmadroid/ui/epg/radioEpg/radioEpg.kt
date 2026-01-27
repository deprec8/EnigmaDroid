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

package io.github.deprec8.enigmadroid.ui.epg.radioEpg

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import io.github.deprec8.enigmadroid.ui.components.NoResults
import io.github.deprec8.enigmadroid.ui.components.insets.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.search.SearchHistory
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBar
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBarDrawerNavigationButton
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBarRemoteControlActionButton
import io.github.deprec8.enigmadroid.ui.epg.components.BouquetMenu
import io.github.deprec8.enigmadroid.ui.epg.components.EpgContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioEpgPage(
    onNavigateToRemoteControl: () -> Unit,
    drawerState: DrawerState,
    radioEpgViewModel: RadioEpgViewModel = hiltViewModel()
) {
    val eventBatchSet by radioEpgViewModel.eventBatchSet.collectAsStateWithLifecycle()
    val filteredEvents by radioEpgViewModel.filteredEvents.collectAsStateWithLifecycle()
    val searchHistory by radioEpgViewModel.searchHistory.collectAsStateWithLifecycle()
    val useSearchHighlighting by radioEpgViewModel.useSearchHighlighting.collectAsStateWithLifecycle()
    val searchInput by radioEpgViewModel.searchInput.collectAsStateWithLifecycle()
    val loadingState by radioEpgViewModel.loadingState.collectAsStateWithLifecycle()
    val bouquets by radioEpgViewModel.bouquets.collectAsStateWithLifecycle()
    val currentBouquet by radioEpgViewModel.currentBouquet.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { eventBatchSet.eventBatches.size })
    val selectedTabIndex = remember {
        derivedStateOf {
            pagerState.currentPage.coerceIn(
                0, (if (eventBatchSet.eventBatches.size - 1 < 0) {
                    0
                } else {
                    eventBatchSet.eventBatches.size - 1
                })
            )
        }
    }


    LaunchedEffect(Unit) {
        radioEpgViewModel.updateLoadingState(false)
    }

    LaunchedEffect(loadingState) {
        if (loadingState == LoadingState.LOADED) {
            radioEpgViewModel.fetchData()
        }
    }

    Scaffold(floatingActionButton = {
        FloatingRefreshButton(loadingState) { radioEpgViewModel.fetchData() }
    }, contentWindowInsets = contentWithDrawerWindowInsets(), topBar = {
        SearchTopAppBar(
            enabled = eventBatchSet.eventBatches.isNotEmpty() && loadingState == LoadingState.LOADED,
            textFieldState = radioEpgViewModel.searchFieldState,
            placeholder = stringResource(R.string.search_epg),
            content = {
                if (filteredEvents != null) {
                    EpgContent(
                        events = filteredEvents !!,
                        paddingValues = PaddingValues(0.dp),
                        showChannelName = true,
                        highlightedWords = if (useSearchHighlighting) searchInput.split(" ")
                            .filter { it.isNotBlank() } else emptyList(),
                        onAddTimerForEvent = { radioEpgViewModel.addTimerForEvent(it) })
                } else {
                    SearchHistory(searchHistory = searchHistory, onTermSearchClick = {
                        radioEpgViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(it)
                        radioEpgViewModel.updateSearchInput()
                    }, onTermInsertClick = {
                        radioEpgViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(
                            it
                        )
                    })
                }
            },
            navigationButton = { searchBarState ->
                SearchTopAppBarDrawerNavigationButton(drawerState, searchBarState)
            },
            actionButtons = {
                Row {
                    BouquetMenu(
                        bouquets,
                        currentBouquet
                    ) { bouquetReference -> radioEpgViewModel.setCurrentBouquet(bouquetReference) }
                    SearchTopAppBarRemoteControlActionButton(onNavigateToRemoteControl = { onNavigateToRemoteControl() })
                }
            },
            onSearch = {
                radioEpgViewModel.updateSearchInput()
            },
            tabBar = {
                if (eventBatchSet.eventBatches.isNotEmpty()) {
                    PrimaryScrollableTabRow(
                        selectedTabIndex = selectedTabIndex.value,
                        divider = { },
                        scrollState = rememberScrollState()
                    ) {
                        eventBatchSet.eventBatches.forEachIndexed { index, eventBatch ->
                            Tab(
                                text = {
                                    Text(
                                        text = eventBatch.name,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                onClick = {
                                    scope.launch { pagerState.animateScrollToPage(index) }
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
        if (eventBatchSet.eventBatches.isNotEmpty()) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) { service ->
                EpgContent(
                    events = eventBatchSet.eventBatches[service].events,
                    innerPadding,
                    onAddTimerForEvent = { radioEpgViewModel.addTimerForEvent(it) })
            }
        } else if (eventBatchSet.result) {
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
                        radioEpgViewModel.updateLoadingState(
                            it
                        )
                    }
                },
                loadingState = loadingState
            )
        }
    }
}