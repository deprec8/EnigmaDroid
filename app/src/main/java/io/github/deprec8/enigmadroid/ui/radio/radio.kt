/*
 * Copyright (C) 2025 deprec8
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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Cast
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import io.github.deprec8.enigmadroid.model.api.Event
import io.github.deprec8.enigmadroid.model.menu.MenuItem
import io.github.deprec8.enigmadroid.model.menu.MenuSection
import io.github.deprec8.enigmadroid.ui.components.ContentListItem
import io.github.deprec8.enigmadroid.ui.components.LoadingScreen
import io.github.deprec8.enigmadroid.ui.components.NoResults
import io.github.deprec8.enigmadroid.ui.components.SearchHistory
import io.github.deprec8.enigmadroid.ui.components.SearchTopAppBar
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.utils.IntentUtils
import io.github.deprec8.enigmadroid.utils.TimestampUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioPage(
    onNavigateToRemoteControl: () -> Unit,
    onNavigateToServiceEPG: (sRef: String, sName: String) -> Unit,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState, radioViewModel: RadioViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val filteredRadioEvents by radioViewModel.filteredEvents.collectAsStateWithLifecycle()
    val allRadioEvents by radioViewModel.allEvents.collectAsStateWithLifecycle()
    val searchHistory by radioViewModel.searchHistory.collectAsStateWithLifecycle()
    val useSearchHighlighting by radioViewModel.useSearchHighlighting.collectAsStateWithLifecycle()

    currentWindowAdaptiveInfo().windowSizeClass
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { allRadioEvents.size })
    val selectedTabIndex = remember {
        derivedStateOf {
            pagerState.currentPage.coerceIn(
                0,
                (if (allRadioEvents.size - 1 < 0) {
                    0
                } else {
                    allRadioEvents.size - 1
                })
            )
        }
    }
    val loadingState by radioViewModel.loadingState.collectAsStateWithLifecycle()
    val searchInput by radioViewModel.searchInput.collectAsStateWithLifecycle()

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
        list: List<Event>,
        paddingValues: PaddingValues,
        showChannelNumbers: Boolean = true, highlightedWords: List<String> = emptyList()
    ) {
        if (list.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(310.dp),
                Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(paddingValues)
                    .imePadding(),
                contentPadding = paddingValues
            ) {
                items(list) { event ->
                    ContentListItem(
                        highlightedWords = highlightedWords,
                        headlineText = event.serviceName,
                        leadingContent = if (showChannelNumbers) {
                            {
                                Text(
                                    text = "${list.indexOf(event) + 1}.",
                                    textAlign = TextAlign.Center,
                                )
                            }
                        } else {
                            null
                        },
                        supportingText = event.title,
                        additionalInfo = TimestampUtils.formatApiTimestampToTime(event.beginTimestamp) + " - " + TimestampUtils.formatApiTimestampToTime(
                            event.beginTimestamp + event.durationInSeconds
                        ),
                        menuSections = listOf(
                            MenuSection(
                                listOf(
                                    MenuItem(
                                        text = stringResource(R.string.stream),
                                        outlinedIcon = Icons.Outlined.Cast,
                                        filledIcon = Icons.Filled.Cast,
                                        action = {
                                            scope.launch {
                                                IntentUtils.playMedia(
                                                    context,
                                                    radioViewModel.buildStreamUrl(event.serviceReference),
                                                    event.serviceName
                                                )
                                            }
                                        }
                                    ),
                                    MenuItem(
                                        text = stringResource(R.string.switch_channel),
                                        outlinedIcon = Icons.Outlined.PlayArrow,
                                        filledIcon = Icons.Filled.PlayArrow,
                                        action = {
                                            radioViewModel.play(event.serviceReference)
                                        }
                                    ),
                                    MenuItem(
                                        text = stringResource(R.string.record),
                                        outlinedIcon = Icons.Outlined.Videocam,
                                        filledIcon = Icons.Filled.Videocam,
                                        action = {
                                            scope.launch {
                                                radioViewModel.addTimer(event)
                                            }
                                        }
                                    ),
                                    MenuItem(
                                        text = context.getString(R.string.view_epg),
                                        outlinedIcon = Icons.AutoMirrored.Outlined.Dvr,
                                        filledIcon = Icons.AutoMirrored.Filled.Dvr,
                                        action = {
                                            onNavigateToServiceEPG(
                                                event.serviceReference,
                                                event.serviceName
                                            )
                                        }
                                    )
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

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                loadingState == LoadingState.LOADED,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(onClick = {
                    radioViewModel.fetchData()
                }) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.refresh_page)
                    )
                }
            }
        },
        contentWindowInsets = contentWithDrawerWindowInsets(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }, topBar = {
            SearchTopAppBar(
                enabled = allRadioEvents.isNotEmpty(),
                textFieldState = radioViewModel.searchFieldState,
                placeholder = stringResource(R.string.search_events),
                content = {
                    if (filteredRadioEvents != null) {
                        Content(
                            list = filteredRadioEvents !!,
                            paddingValues = PaddingValues(0.dp),
                            showChannelNumbers = false,
                            highlightedWords = if (useSearchHighlighting) searchInput.split(" ")
                                .filter { it.isNotBlank() } else emptyList()
                        )
                    } else {
                        SearchHistory(
                            searchHistory = searchHistory,
                            onTermSearchClick = {
                                radioViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(it)
                                radioViewModel.updateSearchInput(selectedTabIndex.value)
                            },
                            onTermInsertClick = {
                                radioViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(
                                    it
                                )
                            }
                        )
                    }
                },
                drawerState = drawerState,
                onNavigateToRemote = { onNavigateToRemoteControl() },
                onSearch = {
                    radioViewModel.updateSearchInput(selectedTabIndex.value)
                },
                tabBar = {
                    if (allRadioEvents.isNotEmpty()) {
                        PrimaryScrollableTabRow(
                            selectedTabIndex = selectedTabIndex.value,
                            divider = { },
                            scrollState = rememberScrollState()
                        ) {
                            allRadioEvents.forEachIndexed { index, eventList ->
                                Tab(
                                    text = {
                                        Text(
                                            text = eventList.bouquetName,
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
                }
            )

        }

    ) { innerPadding ->
        if (allRadioEvents.isNotEmpty()) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) { index ->
                Content(list = allRadioEvents[index].events, innerPadding)
            }
        } else {
            LoadingScreen(
                Modifier
                    .consumeWindowInsets(innerPadding)
                    .padding(innerPadding),
                updateLoadingState = {
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