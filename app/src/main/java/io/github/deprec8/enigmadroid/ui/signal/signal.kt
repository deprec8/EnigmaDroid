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

package io.github.deprec8.enigmadroid.ui.signal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.ui.components.LoadingScreen
import io.github.deprec8.enigmadroid.ui.components.NoResults
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.horizontalSafeContentPadding
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignalPage(
    onNavigateToRemoteControl: () -> Unit,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState, signalViewModel: SignalViewModel = hiltViewModel()
) {

    val scope = rememberCoroutineScope()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val signalInfo by signalViewModel.signalInfo.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val loadingState by signalViewModel.loadingState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()


    LaunchedEffect(Unit) {
        signalViewModel.updateLoadingState(false)
    }

    LaunchedEffect(loadingState) {
        if (loadingState == LoadingState.LOADED) {
            signalViewModel.fetchData()
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
                    signalViewModel.fetchData()
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
        },
        topBar = {
            TopAppBar(
                modifier = Modifier.horizontalSafeContentPadding(true),
                windowInsets = TopAppBarDefaults.windowInsets
                    .only(
                        WindowInsetsSides.Vertical
                    ),
                title = { Text(text = stringResource(R.string.signal)) },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    if (windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.EXPANDED ||
                        windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = stringResource(id = R.string.open_menu)
                            )
                        }
                    }
                }, actions = {
                    IconButton(onClick = { onNavigateToRemoteControl() }) {
                        Icon(
                            Icons.Default.Dialpad,
                            contentDescription = stringResource(id = R.string.open_remote_control)
                        )
                    }
                })
        },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),

        ) { innerPadding ->
        when (signalInfo.inStandby) {
            "false" -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .consumeWindowInsets(innerPadding)
                        .verticalScroll(scrollState)
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                    ) {
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.size(300.dp),
                            strokeCap = StrokeCap.Round,
                            color = MaterialTheme.colorScheme.surfaceContainer
                        )
                        CircularProgressIndicator(
                            progress = {
                                if (signalInfo.agc.isNotBlank()) {
                                    signalInfo.agc.toFloat() / 100
                                } else {
                                    0f
                                }
                            },
                            modifier = Modifier.size(300.dp),
                            strokeCap = StrokeCap.Round
                        )
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center,
                            text = "${(signalInfo.agc)}%",
                            fontSize = 40.sp
                        )
                    }
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.tunertype)) },
                        supportingContent = {
                            Text(
                                text = signalInfo.tunerType
                            )
                        })
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.tunernumber)) },
                        supportingContent = {
                            Text(
                                text = signalInfo.tunerNumber
                            )
                        })
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.snr)) },
                        supportingContent = {
                            Text(
                                text = signalInfo.snr + "%"
                            )
                        })

                }
            }
            "true"  -> {
                if (loadingState == LoadingState.LOADED) {
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
                        loadingState = loadingState,
                        updateLoadingState = {
                            scope.launch {
                                signalViewModel.updateLoadingState(false)
                            }
                        }
                    )
                }
            }
        }
    }
}