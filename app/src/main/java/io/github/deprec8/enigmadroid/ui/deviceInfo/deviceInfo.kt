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

package io.github.deprec8.enigmadroid.ui.deviceInfo

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.model.api.device.DeviceInfo
import io.github.deprec8.enigmadroid.ui.components.FloatingRefreshButton
import io.github.deprec8.enigmadroid.ui.components.LoadingScreen
import io.github.deprec8.enigmadroid.ui.components.NoResults
import io.github.deprec8.enigmadroid.ui.components.RemoteControlActionButton
import io.github.deprec8.enigmadroid.ui.components.insets.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.insets.topAppBarWithDrawerWindowInsets
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceInfoPage(
    onNavigateToRemoteControl: () -> Unit,
    drawerState: DrawerState,
    deviceInfoViewModel: DeviceInfoViewModel = hiltViewModel()
) {

    val loadingState by deviceInfoViewModel.loadingState.collectAsStateWithLifecycle()
    val deviceInfo by deviceInfoViewModel.deviceInfo.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LaunchedEffect(Unit) {
        deviceInfoViewModel.updateLoadingState(false)
    }

    LaunchedEffect(loadingState) {
        if (loadingState == LoadingState.LOADED) {
            deviceInfoViewModel.fetchData()
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingRefreshButton(loadingState) { deviceInfoViewModel.fetchData() }
        },
        contentWindowInsets = contentWithDrawerWindowInsets(),
        topBar = {
            TopAppBar(windowInsets = topAppBarWithDrawerWindowInsets(), title = {
                Text(
                    text = stringResource(id = R.string.deviceinfo),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }, scrollBehavior = scrollBehavior, navigationIcon = {
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
            }, actions = {
                RemoteControlActionButton { onNavigateToRemoteControl() }
            })
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        ) { innerPadding ->
        if (deviceInfo != DeviceInfo()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(310.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(innerPadding),
                contentPadding = innerPadding
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = stringResource(R.string.general),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                item {
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.brand)) },
                        supportingContent = { Text(text = deviceInfo.brand) })
                }
                item {
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.model)) },
                        supportingContent = { Text(text = deviceInfo.model) })
                }
                item {
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.uptime)) },
                        supportingContent = { Text(text = deviceInfo.uptime) })
                }
                item {
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.dinfo_imageversion)) },
                        supportingContent = { Text(text = deviceInfo.imageVersion) })
                }
                item {
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.dinfo_guiversion)) },
                        supportingContent = { Text(text = deviceInfo.enigmaVersion) })

                }
                item {
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.dinfo_frontpversion)) },
                        supportingContent = { Text(text = deviceInfo.fpVersion.toString()) })

                }
                item {
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.kernel_version)) },
                        supportingContent = { Text(text = deviceInfo.kernelVersion) })

                }
                item {
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.dinfo_interfaceversion)) },
                        supportingContent = { Text(text = deviceInfo.webifVersion) })
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = stringResource(id = R.string.tuner),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                items(deviceInfo.tuners) { tuner ->
                    ListItem(headlineContent = { Text(text = tuner.name) }, supportingContent = {
                        Text(
                            text = tuner.type
                        )
                    })
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = stringResource(id = R.string.network),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                items(deviceInfo.interfaces) { iface ->
                    ListItem(headlineContent = { Text(text = iface.name) }, supportingContent = {
                        Text(
                            text = iface.ip
                        )
                    })
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = stringResource(R.string.storage),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                items(deviceInfo.hdds) { hdd ->
                    ListItem(
                        headlineContent = { Text(text = "${hdd.model} (${hdd.mount})") },
                        supportingContent = {
                            Text(
                                text = "${hdd.capacity} (${hdd.free} " + stringResource(
                                    R.string.free
                                ) + ")"
                            )
                        })
                }
            }
        } else if (deviceInfo.result) {
            NoResults(
                Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            )
        } else {
            LoadingScreen(
                Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
                onUpdateLoadingState = {
                    scope.launch {
                        deviceInfoViewModel.updateLoadingState(
                            it
                        )
                    }
                },
                loadingState = loadingState
            )
        }
    }
}