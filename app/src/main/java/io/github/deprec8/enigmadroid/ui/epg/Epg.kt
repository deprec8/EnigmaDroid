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

package io.github.deprec8.enigmadroid.ui.epg

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.common.enums.ContentType
import io.github.deprec8.enigmadroid.data.ConnectionState
import io.github.deprec8.enigmadroid.model.api.Bouquet
import io.github.deprec8.enigmadroid.ui.components.ConnectionDisplay
import io.github.deprec8.enigmadroid.ui.components.FloatingReloadButton
import io.github.deprec8.enigmadroid.ui.components.NoResults
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpgPage(
    contentType: ContentType,
    onNavigateToRemoteControl: () -> Unit,
    drawerState: DrawerState,
    epgViewModel: EpgViewModel = koinViewModel(parameters = { parametersOf(contentType) })
) {
    val eventBatchSet by epgViewModel.eventBatchSet.collectAsStateWithLifecycle()
    val bouquets by epgViewModel.bouquets.collectAsStateWithLifecycle()
    val currentBouquetReference by epgViewModel.currentBouquetReference.collectAsStateWithLifecycle()
    val filteredEvents by epgViewModel.filteredEvents.collectAsStateWithLifecycle()
    val searchHistory by epgViewModel.searchHistory.collectAsStateWithLifecycle()
    val connectionState by epgViewModel.connectionState.collectAsStateWithLifecycle()
    val highlightedWords by epgViewModel.highlightedWords.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { eventBatchSet?.eventBatches?.size ?: 0 })
    val selectedTabIndex by remember {
        derivedStateOf {
            pagerState.currentPage.coerceIn(
                0, ((eventBatchSet?.eventBatches?.size ?: 0) - 1).coerceAtLeast(0)
            )
        }
    }

    ObserveActiveState(epgViewModel)

    Scaffold(floatingActionButton = {
        FloatingReloadButton(connectionState) {
            epgViewModel.fetchData()
        }
    }, contentWindowInsets = contentWithDrawerWindowInsets(), topBar = {
        SearchTopAppBar(
            enabled = eventBatchSet?.eventBatches?.isNotEmpty() == true && connectionState == ConnectionState.CONNECTED,
            textFieldState = epgViewModel.searchFieldState,
            placeholder = stringResource(R.string.search_epg),
            content = {
                filteredEvents?.let {
                    EpgContent(
                        events = it,
                        paddingValues = PaddingValues(0.dp),
                        showChannelName = true,
                        highlightedWords = highlightedWords,
                        onAddTimerForEvent = { event -> epgViewModel.addTimerForEvent(event) })
                } ?: run {
                    SearchHistory(searchHistory = searchHistory, onSearchQuery = {
                        epgViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(it)
                        epgViewModel.updateSearchInput()
                    }, onInsertQuery = {
                        epgViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(
                            it
                        )
                    }, onRemoveItem = {
                        epgViewModel.deleteFromSearchHistory(it)
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
                    ) { bouquetReference -> epgViewModel.setCurrentBouquet(bouquetReference) }
                    RemoteControlActionButton(onNavigateToRemoteControl = { onNavigateToRemoteControl() })
                }
            },
            onSearch = {
                epgViewModel.updateSearchInput()
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
                        onAddTimerForEvent = { epgViewModel.addTimerForEvent(it) })
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
                    epgViewModel.checkConnection()
                },
                connectionState = connectionState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BouquetMenu(
    bouquets: List<Bouquet>?,
    currentBouquetReference: String,
    connectionState: ConnectionState,
    onBouquetChange: (String) -> Unit
) {
    var showMenu by rememberSaveable { mutableStateOf(false) }

    TooltipBox(
        tooltip = {
            PlainTooltip {
                Text(stringResource(R.string.bouquet_menu))
            }
        },
        state = rememberTooltipState(),
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            TooltipAnchorPosition.Below, 4.dp
        )
    ) {
        IconButton(
            onClick = {
                showMenu = true
            },
            enabled = bouquets?.isNotEmpty() == true && connectionState == ConnectionState.CONNECTED
        ) {
            Icon(
                Icons.Default.MoreVert, contentDescription = stringResource(R.string.bouquet_menu)
            )
            DropdownMenu(
                expanded = showMenu, onDismissRequest = { showMenu = false }) {
                bouquets?.forEach { bouquet ->
                    DropdownMenuItem(text = { Text(bouquet.name) }, onClick = {
                        onBouquetChange(bouquet.reference)
                        showMenu = false
                    }, leadingIcon = {
                        if (currentBouquetReference == bouquet.reference) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = stringResource(R.string.current_bouquet)
                            )
                        }
                    })
                }
            }
        }
    }
}