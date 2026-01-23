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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import io.github.deprec8.enigmadroid.ui.components.LoadingScreen
import io.github.deprec8.enigmadroid.ui.components.NoResults
import io.github.deprec8.enigmadroid.ui.components.SearchHistory
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
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
    val epgs by radioEpgViewModel.epgs.collectAsStateWithLifecycle()
    val filteredEvents by radioEpgViewModel.filteredEvents.collectAsStateWithLifecycle()
    val searchHistory by radioEpgViewModel.searchHistory.collectAsStateWithLifecycle()
    val useSearchHighlighting by radioEpgViewModel.useSearchHighlighting.collectAsStateWithLifecycle()
    val searchInput by radioEpgViewModel.searchInput.collectAsStateWithLifecycle()
    val loadingState by radioEpgViewModel.loadingState.collectAsStateWithLifecycle()
    val bouquets by radioEpgViewModel.bouquets.collectAsStateWithLifecycle()
    val currentBouquet by radioEpgViewModel.currentBouquet.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { epgs.eventBatches.size })
    val selectedTabIndex = remember {
        derivedStateOf {
            pagerState.currentPage.coerceIn(
                0, (if (epgs.eventBatches.size - 1 < 0) {
                    0
                } else {
                    epgs.eventBatches.size - 1
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
        AnimatedVisibility(
            loadingState == LoadingState.LOADED, enter = scaleIn(), exit = scaleOut()
        ) {
            FloatingActionButton(onClick = {
                radioEpgViewModel.fetchData()
            }) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.refresh_page)
                )
            }
        }
    }, contentWindowInsets = contentWithDrawerWindowInsets(), topBar = {
        SearchTopAppBar(
            enabled = epgs.eventBatches.isNotEmpty() && loadingState == LoadingState.LOADED,
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
                        onAddTimer = { radioEpgViewModel.addTimer(it) })
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
                        currentBouquet,
                        { bRef -> radioEpgViewModel.setCurrentBouquet(bRef) })
                    SearchTopAppBarRemoteControlActionButton(onNavigateToRemoteControl = { onNavigateToRemoteControl() })
                }
            },
            onSearch = {
                radioEpgViewModel.updateSearchInput()
            },
            tabBar = {
                if (epgs.eventBatches.isNotEmpty()) {
                    PrimaryScrollableTabRow(
                        selectedTabIndex = selectedTabIndex.value,
                        divider = { },
                        scrollState = rememberScrollState()
                    ) {
                        epgs.eventBatches.forEachIndexed { index, epg ->
                            Tab(
                                text = {
                                    Text(
                                        text = epg.name,
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
        if (epgs.eventBatches.isNotEmpty()) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) { service ->
                EpgContent(
                    events = epgs.eventBatches[service].events,
                    innerPadding,
                    onAddTimer = { radioEpgViewModel.addTimer(it) })
            }
        } else if (epgs.result) {
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