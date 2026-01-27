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

package io.github.deprec8.enigmadroid.ui.radio

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Dvr
import androidx.compose.material.icons.automirrored.outlined.Dvr
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Cast
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Videocam
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.model.api.events.Event
import io.github.deprec8.enigmadroid.model.menu.MenuItem
import io.github.deprec8.enigmadroid.model.menu.MenuItemGroup
import io.github.deprec8.enigmadroid.ui.components.FloatingRefreshButton
import io.github.deprec8.enigmadroid.ui.components.LoadingScreen
import io.github.deprec8.enigmadroid.ui.components.NoResults
import io.github.deprec8.enigmadroid.ui.components.content.ContentListItem
import io.github.deprec8.enigmadroid.ui.components.insets.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.search.SearchHistory
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBar
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBarDrawerNavigationButton
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBarRemoteControlActionButton
import io.github.deprec8.enigmadroid.utils.IntentUtils
import io.github.deprec8.enigmadroid.utils.TimestampUtils
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

    val context = LocalContext.current
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

    @Composable
    fun Content(
        events: List<Event>,
        paddingValues: PaddingValues,
        showChannelNumbers: Boolean = true,
        highlightedWords: List<String> = emptyList()
    ) {
        if (events.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(310.dp),
                Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(paddingValues)
                    .imePadding(),
                contentPadding = paddingValues
            ) {
                items(events) { event ->
                    ContentListItem(
                        highlightedWords = highlightedWords,
                        headlineText = event.serviceName,
                        leadingContent = if (showChannelNumbers) {
                            {
                                Text(
                                    text = "${events.indexOf(event) + 1}.",
                                    textAlign = TextAlign.Center,
                                )
                            }
                        } else {
                            null
                        },
                        supportingText = event.title,
                        additionalInfo = "${TimestampUtils.formatApiTimestampToTime(event.beginTimestamp)} - " + TimestampUtils.formatApiTimestampToTime(
                            event.beginTimestamp + event.durationInSeconds
                        ),
                        menuItemGroups = listOf(
                            MenuItemGroup(
                                listOf(
                                    MenuItem(
                                        text = stringResource(R.string.stream),
                                        outlinedIcon = Icons.Outlined.Cast,
                                        filledIcon = Icons.Filled.Cast,
                                        action = {
                                            scope.launch {
                                                IntentUtils.playMedia(
                                                    context,
                                                    radioViewModel.buildLiveStreamUrl(event.serviceReference),
                                                    event.serviceName
                                                )
                                            }
                                        }), MenuItem(
                                        text = stringResource(R.string.switch_channel),
                                        outlinedIcon = Icons.Outlined.PlayArrow,
                                        filledIcon = Icons.Filled.PlayArrow,
                                        action = {
                                            radioViewModel.play(event.serviceReference)
                                        }), MenuItem(
                                        text = stringResource(R.string.record),
                                        outlinedIcon = Icons.Outlined.Videocam,
                                        filledIcon = Icons.Filled.Videocam,
                                        action = {
                                            radioViewModel.addTimerForEvent(event)
                                        }), MenuItem(
                                        text = stringResource(R.string.view_epg),
                                        outlinedIcon = Icons.AutoMirrored.Outlined.Dvr,
                                        filledIcon = Icons.AutoMirrored.Filled.Dvr,
                                        action = {
                                            onNavigateToServiceEpg(
                                                event.serviceReference, event.serviceName
                                            )
                                        })
                                )
                            )
                        ),
                        progress = ((event.nowTimestamp - event.beginTimestamp).toFloat() / event.durationInSeconds),
                        shortDescription = event.shortDescription,
                        longDescription = event.longDescription
                    )
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

    Scaffold(floatingActionButton = {
        FloatingRefreshButton(loadingState, { radioViewModel.fetchData() })
    }, contentWindowInsets = contentWithDrawerWindowInsets(), topBar = {
        SearchTopAppBar(
            enabled = eventBatches.isNotEmpty(),
            textFieldState = radioViewModel.searchFieldState,
            placeholder = stringResource(R.string.search_events),
            content = {
                if (filteredEvents != null) {
                    Content(
                        events = filteredEvents !!,
                        paddingValues = PaddingValues(0.dp),
                        showChannelNumbers = false,
                        highlightedWords = if (useSearchHighlighting) searchInput.split(" ")
                            .filter { it.isNotBlank() } else emptyList())
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
                SearchTopAppBarRemoteControlActionButton(onNavigateToRemoteControl = { onNavigateToRemoteControl() })
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
                Content(events = eventBatches[index].events, innerPadding)
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