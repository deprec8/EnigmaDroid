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

package io.github.deprec8.enigmadroid.ui.current

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.ui.components.LoadingScreen
import io.github.deprec8.enigmadroid.ui.components.insets.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.insets.topAppBarWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.utils.IntentUtils
import io.github.deprec8.enigmadroid.utils.TimestampUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentPage(
    onNavigateToRemoteControl: () -> Unit,
    onNavigateToServiceEpg: (serviceReference: String, serviceName: String) -> Unit,
    drawerState: DrawerState,
    currentViewModel: CurrentViewModel = hiltViewModel()
) {

    val currentEventInfo by currentViewModel.currentInfo.collectAsStateWithLifecycle()
    val loadingState by currentViewModel.loadingState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current
    val scrollState = rememberScrollState()


    LaunchedEffect(Unit) {
        currentViewModel.updateLoadingState(false)
    }

    LaunchedEffect(loadingState) {
        if (loadingState == LoadingState.LOADED) {
            currentViewModel.fetchData()
        }
    }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                loadingState == LoadingState.LOADED, enter = scaleIn(), exit = scaleOut()
            ) {
                FloatingActionButton(onClick = {
                    currentViewModel.fetchData()
                }) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.refresh_page)
                    )
                }
            }
        },
        contentWindowInsets = contentWithDrawerWindowInsets(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.current),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                windowInsets = topAppBarWithDrawerWindowInsets(),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    if (! windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) || ! windowSizeClass.isHeightAtLeastBreakpoint(
                            WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND
                        )
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = stringResource(R.string.open_menu)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToRemoteControl() }) {
                        Icon(
                            Icons.Default.Dialpad,
                            contentDescription = stringResource(R.string.open_remote_control)
                        )
                    }
                })
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        when (currentEventInfo.info.result) {
            true -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .consumeWindowInsets(innerPadding)
                        .verticalScroll(scrollState)
                        .padding(innerPadding)
                ) {
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.channel)) },
                        supportingContent = {
                            Text(text = currentEventInfo.now.serviceName)
                        })
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.provider)) },
                        supportingContent = {
                            Text(text = currentEventInfo.now.provider)
                        })
                    ListItem(
                        overlineContent = {
                            Text(text = stringResource(R.string.now))
                        },
                        headlineContent = { Text(text = currentEventInfo.now.title) },
                        supportingContent = {
                            Column {
                                if (currentEventInfo.now.shortDescription.isNotBlank()) {
                                    Text(
                                        text = currentEventInfo.now.shortDescription, maxLines = 1
                                    )
                                }
                                Text(
                                    text = TimestampUtils.formatApiTimestampToTime(
                                        currentEventInfo.now.beginTimestamp
                                    ) + " - " + TimestampUtils.formatApiTimestampToTime(
                                        currentEventInfo.now.beginTimestamp + currentEventInfo.now.durationInSeconds
                                    ), maxLines = 1
                                )
                            }
                        })
                    LinearProgressIndicator(
                        progress = {
                            if (currentEventInfo.now.durationInSeconds > 0) {
                                ((currentEventInfo.now.nowTimestamp - currentEventInfo.now.beginTimestamp).toFloat() / currentEventInfo.now.durationInSeconds)
                            } else {
                                0f
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp, end = 16.dp
                            ),
                        strokeCap = StrokeCap.Round,
                    )
                    Spacer(Modifier.size(8.dp))
                    ListItem(
                        overlineContent = {
                            Text(text = stringResource(R.string.next))
                        },
                        headlineContent = { Text(text = currentEventInfo.next.title) },
                        supportingContent = {
                            Column {
                                if (currentEventInfo.next.shortDescription.isNotBlank()) {
                                    Text(
                                        text = currentEventInfo.next.shortDescription, maxLines = 1
                                    )
                                }
                                Text(
                                    text = TimestampUtils.formatApiTimestampToTime(
                                        currentEventInfo.next.beginTimestamp
                                    ) + " - " + TimestampUtils.formatApiTimestampToTime(
                                        currentEventInfo.next.beginTimestamp + currentEventInfo.next.durationInSeconds
                                    ), maxLines = 1
                                )
                            }
                        })
                    Spacer(Modifier.size(16.dp))
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                IntentUtils.playMedia(
                                    context,
                                    currentViewModel.buildLiveStreamUrl(currentEventInfo.now.serviceReference),
                                    currentEventInfo.now.serviceName
                                )
                            }
                        }, Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(text = stringResource(R.string.stream_current))
                    }
                    Spacer(Modifier.size(16.dp))
                    OutlinedButton(
                        onClick = {
                            onNavigateToServiceEpg(
                                currentEventInfo.now.serviceReference,
                                currentEventInfo.now.serviceName
                            )
                        }, Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(text = stringResource(R.string.view_epg_for_current_channel))
                    }
                    Spacer(Modifier.size(16.dp))
                }
            }
            false -> {
                Box(
                    modifier = Modifier
                        .consumeWindowInsets(innerPadding)
                        .fillMaxSize()
                        .padding(innerPadding), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.device_is_not_playing_any_channel),
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                LoadingScreen(
                    Modifier
                        .consumeWindowInsets(innerPadding)
                        .padding(innerPadding),
                    onUpdateLoadingState = {
                        scope.launch {
                            currentViewModel.updateLoadingState(
                                it
                            )
                        }
                    },
                    loadingState = loadingState
                )
            }
        }
    }
}