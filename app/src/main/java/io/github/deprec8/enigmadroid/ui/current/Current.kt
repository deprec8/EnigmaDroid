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

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.common.enums.LoadingState
import io.github.deprec8.enigmadroid.model.api.CurrentInfo
import io.github.deprec8.enigmadroid.ui.components.FloatingReloadButton
import io.github.deprec8.enigmadroid.ui.components.LoadingScreen
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.navigation.DrawerNavigationButton
import io.github.deprec8.enigmadroid.ui.components.navigation.RemoteControlActionButton
import io.github.deprec8.enigmadroid.ui.components.topAppBarWithDrawerWindowInsets
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
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LaunchedEffect(Unit) {
        currentViewModel.updateLoadingState(false)
    }

    LaunchedEffect(loadingState) {
        if (loadingState == LoadingState.LOADED) {
            currentViewModel.fetchData()
        }
    }

    Scaffold(floatingActionButton = {
        FloatingReloadButton(loadingState) { currentViewModel.fetchData(isForced = true) }
    }, contentWindowInsets = contentWithDrawerWindowInsets(), topBar = {
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
                DrawerNavigationButton(drawerState)
            },
            actions = {
                RemoteControlActionButton { onNavigateToRemoteControl() }
            })
    }) { innerPadding ->
        if (currentEventInfo != null && loadingState == LoadingState.LOADED) {
            CurrentContent(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                currentEventInfo ?: CurrentInfo(),
                innerPadding,
                onBuildLiveStreamUrl = {
                    currentViewModel.buildLiveStreamUrl(it)
                },
                onNavigateToServiceEpg = { serviceReference, serviceName ->
                    onNavigateToServiceEpg(
                        serviceReference, serviceName
                    )
                })
        } else {
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