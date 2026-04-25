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

package io.github.deprec8.enigmadroid.ui.epg.serviceEpg

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.common.enums.LoadingState
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.loading.FloatingReloadButton
import io.github.deprec8.enigmadroid.ui.components.loading.LoadingScreen
import io.github.deprec8.enigmadroid.ui.components.navigation.ArrowNavigationButton
import io.github.deprec8.enigmadroid.ui.components.search.SearchHistory
import io.github.deprec8.enigmadroid.ui.components.search.SearchTopAppBar
import io.github.deprec8.enigmadroid.ui.epg.components.EpgContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceEpgPage(
    serviceName: String,
    onNavigateBack: () -> Unit,
    serviceEpgViewModel: ServiceEpgViewModel = hiltViewModel()
) {
    val eventBatch by serviceEpgViewModel.eventBatch.collectAsStateWithLifecycle()
    val loadingState by serviceEpgViewModel.loadingState.collectAsStateWithLifecycle()
    val filteredEvents by serviceEpgViewModel.filteredEvents.collectAsStateWithLifecycle()
    val searchHistory by serviceEpgViewModel.searchHistory.collectAsStateWithLifecycle()
    val highlightedWords by serviceEpgViewModel.highlightedWords.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        serviceEpgViewModel.updateLoadingState(false)
    }

    LaunchedEffect(loadingState) {
        if (loadingState == LoadingState.LOADED) {
            serviceEpgViewModel.fetchData()
        }
    }

    Scaffold(floatingActionButton = {
        FloatingReloadButton(loadingState) { serviceEpgViewModel.fetchData(isForced = true) }
    }, contentWindowInsets = contentWithDrawerWindowInsets(), topBar = {
        SearchTopAppBar(
            enabled = eventBatch?.events?.isNotEmpty() == true && loadingState == LoadingState.LOADED,
            textFieldState = serviceEpgViewModel.searchFieldState,
            placeholder = stringResource(R.string.search_epg_from, serviceName),
            content = {
                filteredEvents?.let {
                    EpgContent(
                        events = it,
                        paddingValues = PaddingValues(0.dp),
                        showChannelName = true,
                        highlightedWords = highlightedWords,
                        onAddTimerForEvent = { event -> serviceEpgViewModel.addTimerForEvent(event) })
                } ?: run {
                    SearchHistory(searchHistory = searchHistory, onTermSearchClick = {
                        serviceEpgViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(it)
                        serviceEpgViewModel.updateSearchInput()
                    }, onTermInsertClick = {
                        serviceEpgViewModel.searchFieldState.setTextAndPlaceCursorAtEnd(
                            it
                        )
                    })
                }
            },
            navigationButton = {
                ArrowNavigationButton { onNavigateBack() }
            },
            onSearch = {
                serviceEpgViewModel.updateSearchInput()
            })
    }) { innerPadding ->
        if (eventBatch != null && loadingState == LoadingState.LOADED) {
            EpgContent(
                events = eventBatch?.events ?: emptyList(),
                innerPadding,
                onAddTimerForEvent = { serviceEpgViewModel.addTimerForEvent(it) })
        } else {
            LoadingScreen(
                Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
                onUpdateLoadingState = {
                    scope.launch {
                        serviceEpgViewModel.updateLoadingState(
                            it
                        )
                    }
                },
                loadingState = loadingState
            )
        }
    }
}