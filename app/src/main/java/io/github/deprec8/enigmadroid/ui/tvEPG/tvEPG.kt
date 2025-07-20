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

package io.github.deprec8.enigmadroid.ui.tvEPG

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.Timer
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.objects.LoadingState
import io.github.deprec8.enigmadroid.model.EPGEvent
import io.github.deprec8.enigmadroid.model.menu.MenuItem
import io.github.deprec8.enigmadroid.model.menu.MenuSection
import io.github.deprec8.enigmadroid.ui.components.ContentListItem
import io.github.deprec8.enigmadroid.ui.components.LoadingScreen
import io.github.deprec8.enigmadroid.ui.components.NoResults
import io.github.deprec8.enigmadroid.ui.components.SearchHistory
import io.github.deprec8.enigmadroid.ui.components.SearchTopAppBar
import io.github.deprec8.enigmadroid.ui.components.calculateSearchTopAppBarContentPaddingValues
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.horizontalSafeContentPadding
import io.github.deprec8.enigmadroid.utils.IntentUtils
import io.github.deprec8.enigmadroid.utils.TimestampUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TVEPGPage(
    onNavigateToRemoteControl: () -> Unit,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    tvEPGViewModel: TVEPGViewModel = hiltViewModel()
) {

    val scope = rememberCoroutineScope()

    val epgs by tvEPGViewModel.epgs.collectAsStateWithLifecycle()
    val active by tvEPGViewModel.active.collectAsStateWithLifecycle()
    val filteredEPGEvents by tvEPGViewModel.filteredEPGEvents.collectAsStateWithLifecycle()
    val searchHistory by tvEPGViewModel.searchHistory.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { epgs.size })
    val selectedTabIndex = remember {
        derivedStateOf {
            pagerState.currentPage.coerceIn(
                0,
                (if (epgs.size - 1 < 0) {
                    0
                } else {
                    epgs.size - 1
                })
            )
        }
    }
    val useSearchHighlighting by tvEPGViewModel.useSearchHighlighting.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val loadingState by tvEPGViewModel.loadingState.collectAsStateWithLifecycle()
    val searchInput by tvEPGViewModel.searchInput.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        tvEPGViewModel.updateLoadingState(false)
    }
    LaunchedEffect(loadingState) {
        if (loadingState == LoadingState.LOADED) {
            tvEPGViewModel.fetchData()
        }
    }

    @Composable
    fun Content(
        list: List<EPGEvent>,
        paddingValues: PaddingValues,
        showChannelName: Boolean = false, highlightedWords: List<String> = emptyList()
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
                        headlineText = event.title,
                        supportingText = event.date,
                        overlineText = if (event.beginTimestamp * 1000 <= System.currentTimeMillis()) {
                            stringResource(R.string.now)
                        } else {
                            TimestampUtils.formatApiTimestampToTime(
                                event.beginTimestamp
                            )
                        } + if (showChannelName) {
                            " - " + event.serviceName
                        } else {
                            ""
                        },
                        shortDescription = event.shortDescription,
                        longDescription = event.longDescription,
                        menuSections = if (event.beginTimestamp * 1000 > System.currentTimeMillis()) {
                            listOf(
                                MenuSection(
                                    listOf(
                                        MenuItem(
                                            text = stringResource(R.string.add_timer),
                                            outlinedIcon = Icons.Outlined.Timer,
                                            filledIcon = Icons.Filled.Timer,
                                            action = {
                                                scope.launch {
                                                    tvEPGViewModel.addTimer(
                                                        event
                                                    )
                                                }
                                            }
                                        ),
                                        MenuItem(
                                            text = context.getString(R.string.add_reminder),
                                            outlinedIcon = Icons.Outlined.AddAlert,
                                            filledIcon = Icons.Filled.AddAlert,
                                            action = {
                                                scope.launch {
                                                    IntentUtils.addReminder(context, event)
                                                }
                                            }
                                        )
                                    )
                                )
                            )
                        } else {
                            null
                        }
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
                loadingState == LoadingState.LOADED && ! active,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(onClick = {
                    tvEPGViewModel.fetchData()
                }, modifier = Modifier.horizontalSafeContentPadding(true)) {
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
                enabled = epgs.isNotEmpty(),
                expanded = active,
                onExpandedChange = { tvEPGViewModel.updateActive(it) },
                input = tvEPGViewModel.input,
                onInputChange = { tvEPGViewModel.updateInput(it) },
                placeholder = stringResource(R.string.search_epg),
                content = {
                    if (filteredEPGEvents != null) {
                        Content(
                            list = filteredEPGEvents !!,
                            paddingValues = calculateSearchTopAppBarContentPaddingValues(),
                            showChannelName = true,
                            highlightedWords = if (useSearchHighlighting) searchInput.split(" ")
                                .filter { it.isNotBlank() } else emptyList()
                        )
                    } else {
                        SearchHistory(
                            searchHistory = searchHistory,
                            onTermSearchClick = {
                                tvEPGViewModel.updateInput(it)
                                tvEPGViewModel.updateSearchInput()
                            },
                            onTermInsertClick = { tvEPGViewModel.updateInput(it) }
                        )
                    }
                },
                drawerState = drawerState,
                onNavigateToRemote = { onNavigateToRemoteControl() },
                onSearch = {
                    tvEPGViewModel.updateSearchInput()
                },
                tabBar = {
                    if (epgs.isNotEmpty()) {
                        PrimaryScrollableTabRow(
                            selectedTabIndex = selectedTabIndex.value,
                            divider = { },
                            scrollState = rememberScrollState()
                        ) {
                            epgs.forEachIndexed { index, epg ->
                                Tab(
                                    text = {
                                        Text(
                                            text = epg.serviceName,
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
                }
            )
        }

    ) { innerPadding ->
        if (epgs.isNotEmpty()) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) { service ->
                Content(list = epgs[service].events, innerPadding)
            }
        } else {
            LoadingScreen(
                Modifier
                    .consumeWindowInsets(innerPadding)
                    .padding(innerPadding),
                updateLoadingState = {
                    scope.launch {
                        tvEPGViewModel.updateLoadingState(
                            it
                        )
                    }
                },
                loadingState = loadingState
            )
        }
    }
}