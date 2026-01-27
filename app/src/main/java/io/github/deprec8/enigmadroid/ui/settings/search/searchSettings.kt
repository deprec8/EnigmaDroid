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

package io.github.deprec8.enigmadroid.ui.settings.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.ui.components.insets.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.insets.topAppBarWithDrawerWindowInsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSettingsPage(
    onNavigateBack: () -> Unit, searchSettingsViewModel: SearchSettingsViewModel = hiltViewModel()
) {

    val tvSearchHistory by searchSettingsViewModel.tvSearchHistory.collectAsStateWithLifecycle()
    val radioSearchHistory by searchSettingsViewModel.radioSearchHistory.collectAsStateWithLifecycle()
    val moviesSearchHistory by searchSettingsViewModel.moviesSearchHistory.collectAsStateWithLifecycle()
    val timersSearchHistory by searchSettingsViewModel.timersSearchHistory.collectAsStateWithLifecycle()
    val tvEpgSearchHistory by searchSettingsViewModel.tvEpgSearchHistory.collectAsStateWithLifecycle()
    val radioEpgSearchHistory by searchSettingsViewModel.radioEpgSearchHistory.collectAsStateWithLifecycle()
    val useSearchHistories by searchSettingsViewModel.useSearchHistories.collectAsStateWithLifecycle()
    val useSearchHighlighting by searchSettingsViewModel.useSearchHighlighting.collectAsStateWithLifecycle()

    var showSearchHistoriesDialog by rememberSaveable { mutableStateOf(false) }
    val searchHistoriesDialogScrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scrollState = rememberScrollState()

    Scaffold(
        contentWindowInsets = contentWithDrawerWindowInsets(),
        topBar = {
            TopAppBar(windowInsets = topAppBarWithDrawerWindowInsets(), title = {
                Text(
                    text = stringResource(R.string.search),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }, scrollBehavior = scrollBehavior, navigationIcon = {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.navigate_back)
                    )
                }
            })
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
                .verticalScroll(scrollState)
                .padding(innerPadding)
        ) {
            useSearchHistories?.let {
                ListItem(headlineContent = {
                    Text(stringResource(R.string.use_search_histories))
                }, supportingContent = {
                    Text(stringResource(R.string.disabling_this_will_clear_all_search_histories))
                }, trailingContent = {
                    Switch(
                        checked = it, onCheckedChange = { value ->
                            searchSettingsViewModel.setUseSearchHistory(value)
                        })
                })
            }
            ListItem(
                headlineContent = {
                    Text(stringResource(R.string.clear_search_histories))
                }, supportingContent = {
                    Text(stringResource(R.string.select_which_search_histories_to_clear))
                }, modifier = Modifier.clickable(onClick = { showSearchHistoriesDialog = true })
            )
            useSearchHighlighting?.let {
                ListItem(headlineContent = {
                    Text(stringResource(R.string.highlight_matches))
                }, supportingContent = {
                    Text(stringResource(R.string.highlight_matches_in_search_results))
                }, trailingContent = {
                    Switch(
                        checked = it, onCheckedChange = { value ->
                            searchSettingsViewModel.setUseSearchHighlighting(value)
                        })
                })
            }
        }
    }

    if (showSearchHistoriesDialog) {
        var tv by rememberSaveable { mutableStateOf(false) }
        var radio by rememberSaveable { mutableStateOf(false) }
        var movies by rememberSaveable { mutableStateOf(false) }
        var timers by rememberSaveable { mutableStateOf(false) }
        var tvEpg by rememberSaveable { mutableStateOf(false) }
        var radioEpg by rememberSaveable { mutableStateOf(false) }

        fun isAnyEnabled(): Boolean {
            return tvSearchHistory.isNotEmpty() || radioSearchHistory.isNotEmpty() || moviesSearchHistory.isNotEmpty() || timersSearchHistory.isNotEmpty() || tvEpgSearchHistory.isNotEmpty() || radioEpgSearchHistory.isNotEmpty()
        }


        fun setAllEnabled(state: Boolean) {
            if (tvSearchHistory.isNotEmpty()) {
                tv = state
            }
            if (radioSearchHistory.isNotEmpty()) {
                radio = state
            }
            if (moviesSearchHistory.isNotEmpty()) {
                movies = state
            }
            if (timersSearchHistory.isNotEmpty()) {
                timers = state
            }
            if (tvEpgSearchHistory.isNotEmpty()) {
                tvEpg = state
            }
            if (radioEpgSearchHistory.isNotEmpty()) {
                radioEpg = state
            }
        }

        fun getAllEnabled(): Boolean {
            if (! isAnyEnabled()) {
                return false
            }
            var allEnabled = true
            if (tvSearchHistory.isNotEmpty()) allEnabled = allEnabled && tv
            if (radioSearchHistory.isNotEmpty()) allEnabled = allEnabled && radio
            if (moviesSearchHistory.isNotEmpty()) allEnabled = allEnabled && movies
            if (timersSearchHistory.isNotEmpty()) allEnabled = allEnabled && timers
            if (tvEpgSearchHistory.isNotEmpty()) allEnabled = allEnabled && tvEpg
            if (radioEpgSearchHistory.isNotEmpty()) allEnabled = allEnabled && radioEpg
            return allEnabled
        }

        AlertDialog(
            onDismissRequest = { showSearchHistoriesDialog = false },
            title = { Text(stringResource(R.string.clear_search_histories)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        searchSettingsViewModel.clearSearchHistory(
                            tv, radio, movies, timers, tvEpg, radioEpg
                        )
                        showSearchHistoriesDialog = false
                    }, enabled = tv || radio || movies || timers || tvEpg || radioEpg
                ) { Text(stringResource(R.string.clear)) }
            },
            dismissButton = {
                TextButton(onClick = { showSearchHistoriesDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            text = {
                Column(Modifier.verticalScroll(searchHistoriesDialogScrollState)) {
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(stringResource(R.string.all)) },
                        trailingContent = {
                            Checkbox(
                                enabled = isAnyEnabled(),
                                checked = getAllEnabled(),
                                onCheckedChange = {
                                    setAllEnabled(it)
                                })
                        })
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(stringResource(R.string.tv)) },
                        trailingContent = {
                            Checkbox(
                                enabled = tvSearchHistory.isNotEmpty(),
                                checked = tv,
                                onCheckedChange = { tv = it })
                        })
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(stringResource(R.string.radio)) },
                        trailingContent = {
                            Checkbox(
                                enabled = radioSearchHistory.isNotEmpty(),
                                checked = radio,
                                onCheckedChange = { radio = it })
                        })
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(stringResource(R.string.movies)) },
                        trailingContent = {
                            Checkbox(
                                enabled = moviesSearchHistory.isNotEmpty(),
                                checked = movies,
                                onCheckedChange = { movies = it })
                        })
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(stringResource(R.string.timers)) },
                        trailingContent = {
                            Checkbox(
                                enabled = timersSearchHistory.isNotEmpty(),
                                checked = timers,
                                onCheckedChange = { timers = it })
                        })
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(stringResource(R.string.tv_epg)) },
                        trailingContent = {
                            Checkbox(
                                enabled = tvEpgSearchHistory.isNotEmpty(),
                                checked = tvEpg,
                                onCheckedChange = { tvEpg = it })
                        })
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(stringResource(R.string.radio_epg)) },
                        trailingContent = {
                            Checkbox(
                                enabled = radioEpgSearchHistory.isNotEmpty(),
                                checked = radioEpg,
                                onCheckedChange = { radioEpg = it })
                        })
                }
            })
    }
}