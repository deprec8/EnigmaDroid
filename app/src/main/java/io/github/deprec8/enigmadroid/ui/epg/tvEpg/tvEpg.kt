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

package io.github.deprec8.enigmadroid.ui.epg.tvEpg

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import io.github.deprec8.enigmadroid.ui.components.NoResults
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.loading.FloatingReloadButton
import io.github.deprec8.enigmadroid.ui.components.loading.LoadingScreen
import io.github.deprec8.enigmadroid.ui.components.navigation.RemoteControlActionButton
import io.github.deprec8.enigmadroid.ui.components.search.SearchHistory
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBar
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBarDrawerNavigationButton
import io.github.deprec8.enigmadroid.ui.epg.components.BouquetMenu
import io.github.deprec8.enigmadroid.ui.epg.components.EpgContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvEpgPage(
    onNavigateToRemoteControl: () -> Unit,
    drawerState: DrawerState,
    tvEpgViewModel: TvEpgViewModel = hiltViewModel()
) {
    val eventBatchSet by tvEpgViewModel.eventBatchSet.collectAsStateWithLifecycle()
    val bouquets by tvEpgViewModel.bouquets.collectAsStateWithLifecycle()
    val currentBouquetReference by tvEpgViewModel.currentBouquetReference.collectAsStateWithLifecycle()
    val filteredEvents by tvEpgViewModel.filteredEvents.collectAsStateWithLifecycle()
    val searchHistory by tvEpgViewModel.searchHistory.collectAsStateWithLifecycle()
    val loadingState by tvEpgViewModel.loadingState.collectAsStateWithLifecycle()
    val highlightedWords by tvEpgViewModel.highlightedWords.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { eventBatchSet.eventBatches.size })
    val selectedTabIndex by remember {
        derivedStateOf {
            pagerState.currentPage.coerceIn(
                0,
                (eventBatchSet.eventBatches.size - 1).coerceAtLeast(0)
            )
        }
    }

    LaunchedEffect(Unit) {
        tvEpgViewModel.updateLoadingState(false)
    }

    LaunchedEffect(loadingState) {
        if (loadingState == LoadingState.LOADED) {
            tvEpgViewModel.fetchData()
        }
    }

    Scaffold(floatingActionButton = {
        FloatingReloadButton(loadingState) { tvEpgViewModel.fetchData() }
    }, contentWindowInsets = contentWithDrawerWindowInsets(), topBar = {
        SearchTopAppBar(
            enabled = eventBatchSet.eventBatches.isNotEmpty() && loadingState == LoadingState.LOADED,
            textFieldState = tvEpgViewModel.searchFieldState,
            placeholder = stringResource(R.string.search_epg),
            content = {
                if (filteredEvents != null) {
                    EpgContent(
                        events = filteredEvents !!,
                        paddingValues = PaddingValues(0.dp),
                        showChannelName = true,
                        highlightedWords = highlightedWords,
                        onAddTimerForEvent = { tvEpgViewModel.addTimerForEvent(it) })
                } else {
                    SearchHistory(searchHistory = searchHistory, onTermSearchClick = {
                        tvEpgViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(it)
                        tvEpgViewModel.updateSearchInput()
                    }, onTermInsertClick = {
                        tvEpgViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(
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
                        bouquets, currentBouquetReference, loadingState
                    ) { bouquetReference -> tvEpgViewModel.setCurrentBouquet(bouquetReference) }
                    RemoteControlActionButton(onNavigateToRemoteControl = { onNavigateToRemoteControl() })
                }
            },
            onSearch = {
                tvEpgViewModel.updateSearchInput()
            },
            tabBar = {
                if (eventBatchSet.eventBatches.isNotEmpty() && loadingState == LoadingState.LOADED) {
                    ContentTabRow(selectedTabIndex) {
                        eventBatchSet.eventBatches.forEachIndexed { index, eventBatch ->
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
        if (eventBatchSet.eventBatches.isNotEmpty() && loadingState == LoadingState.LOADED) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) { service ->
                EpgContent(
                    events = eventBatchSet.eventBatches[service].events,
                    innerPadding,
                    onAddTimerForEvent = { tvEpgViewModel.addTimerForEvent(it) })
            }
        } else if (eventBatchSet.result && loadingState == LoadingState.LOADED) {
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
                        tvEpgViewModel.updateLoadingState(
                            it
                        )
                    }
                },
                loadingState = loadingState
            )
        }
    }
}