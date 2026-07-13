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

package io.github.deprec8.enigmadroid.ui.epg.radio

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.ConnectionState
import io.github.deprec8.enigmadroid.ui.components.ConnectionDisplay
import io.github.deprec8.enigmadroid.ui.components.FloatingReloadButton
import io.github.deprec8.enigmadroid.ui.components.NoResults
import io.github.deprec8.enigmadroid.ui.components.content.ContentTab
import io.github.deprec8.enigmadroid.ui.components.content.ContentTabRow
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.navigation.RemoteControlActionButton
import io.github.deprec8.enigmadroid.ui.components.search.SearchHistory
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBar
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBarDrawerNavigationButton
import io.github.deprec8.enigmadroid.ui.epg.components.BouquetMenu
import io.github.deprec8.enigmadroid.ui.epg.components.EpgContent
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioEpgPage(
    onNavigateToRemoteControl: () -> Unit,
    drawerState: DrawerState,
    radioEpgViewModel: RadioEpgViewModel = koinViewModel()
) {
    val eventBatchSet by radioEpgViewModel.eventBatchSet.collectAsStateWithLifecycle()
    val filteredEvents by radioEpgViewModel.filteredEvents.collectAsStateWithLifecycle()
    val searchHistory by radioEpgViewModel.searchHistory.collectAsStateWithLifecycle()
    val highlightedWords by radioEpgViewModel.highlightedWords.collectAsStateWithLifecycle()
    val connectionState by radioEpgViewModel.connectionState.collectAsStateWithLifecycle()
    val bouquets by radioEpgViewModel.bouquets.collectAsStateWithLifecycle()
    val currentBouquetReference by radioEpgViewModel.currentBouquetReference.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { eventBatchSet?.eventBatches?.size ?: 0 })
    val selectedTabIndex by remember {
        derivedStateOf {
            pagerState.currentPage.coerceIn(
                0, ((eventBatchSet?.eventBatches?.size ?: 0) - 1).coerceAtLeast(0)
            )
        }
    }

    LaunchedEffect(Unit) {
        radioEpgViewModel.checkConnection(false)
    }

    LaunchedEffect(connectionState) {
        if (connectionState == ConnectionState.CONNECTED) {
            radioEpgViewModel.fetchData()
        }
    }

    Scaffold(floatingActionButton = {
        FloatingReloadButton(connectionState) {
            radioEpgViewModel.fetchData(isForced = true)
        }
    }, contentWindowInsets = contentWithDrawerWindowInsets(), topBar = {
        SearchTopAppBar(
            enabled = eventBatchSet?.eventBatches?.isNotEmpty() == true && connectionState == ConnectionState.CONNECTED,
            textFieldState = radioEpgViewModel.searchFieldState,
            placeholder = stringResource(R.string.search_epg),
            content = {
                filteredEvents?.let {
                    EpgContent(
                        events = it,
                        paddingValues = PaddingValues(0.dp),
                        showChannelName = true,
                        highlightedWords = highlightedWords,
                        onAddTimerForEvent = { event -> radioEpgViewModel.addTimerForEvent(event) })
                } ?: run {
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
                        bouquets, currentBouquetReference, connectionState
                    ) { bouquetReference -> radioEpgViewModel.setCurrentBouquet(bouquetReference) }
                    RemoteControlActionButton(onNavigateToRemoteControl = { onNavigateToRemoteControl() })
                }
            },
            onSearch = {
                radioEpgViewModel.updateSearchInput()
            },
            actionBar = {
                if (eventBatchSet?.eventBatches?.isNotEmpty() == true && connectionState == ConnectionState.CONNECTED) {
                    ContentTabRow(selectedTabIndex) {
                        eventBatchSet?.eventBatches?.forEachIndexed { index, eventBatch ->
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
        if (eventBatchSet != null && connectionState == ConnectionState.CONNECTED) {
            if (eventBatchSet?.eventBatches?.isNotEmpty() == true) {
                HorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    state = pagerState,
                ) { service ->
                    EpgContent(
                        events = eventBatchSet?.eventBatches[service]?.events ?: emptyList(),
                        innerPadding,
                        onAddTimerForEvent = { radioEpgViewModel.addTimerForEvent(it) })
                }
            } else {
                NoResults(
                    Modifier
                        .consumeWindowInsets(innerPadding)
                        .padding(innerPadding)
                )
            }
        } else {
            ConnectionDisplay(
                Modifier
                    .consumeWindowInsets(innerPadding)
                    .padding(innerPadding),
                onCheckConnection = {
                    radioEpgViewModel.checkConnection(
                        it
                    )
                },
                connectionState = connectionState
            )
        }
    }
}