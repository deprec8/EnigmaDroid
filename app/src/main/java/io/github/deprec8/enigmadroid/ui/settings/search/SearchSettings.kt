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
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.common.enums.ContentType
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.navigation.ArrowNavigationButton
import io.github.deprec8.enigmadroid.ui.components.topAppBarWithDrawerWindowInsets
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSettingsPage(
    onNavigateBack: () -> Unit, searchSettingsViewModel: SearchSettingsViewModel = koinViewModel()
) {

    val typesWithHistory by searchSettingsViewModel.typesWithHistory.collectAsStateWithLifecycle()
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
                ArrowNavigationButton { onNavigateBack() }
            })
        },
        modifier = Modifier,
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            ListItem(headlineContent = {
                Text(stringResource(R.string.use_search_histories))
            }, supportingContent = {
                Text(stringResource(R.string.disabling_this_will_clear_all_search_histories))
            }, trailingContent = {
                Switch(
                    checked = useSearchHistories, onCheckedChange = { value ->
                        searchSettingsViewModel.setUseSearchHistory(value)
                    })
            }, modifier = Modifier.clickable {
                searchSettingsViewModel.setUseSearchHistory(!useSearchHistories)
            })
            ListItem(
                headlineContent = {
                    Text(stringResource(R.string.view_search_histories))
                }, supportingContent = {
                    Text(stringResource(R.string.view_and_clear_your_search_histories))
                }, modifier = Modifier.clickable(
                    onClick = { showSearchHistoriesDialog = true })
            )
            ListItem(headlineContent = {
                Text(stringResource(R.string.highlight_matches))
            }, supportingContent = {
                Text(stringResource(R.string.highlight_matches_in_search_results))
            }, trailingContent = {
                Switch(
                    checked = useSearchHighlighting, onCheckedChange = { value ->
                        searchSettingsViewModel.setUseSearchHighlighting(value)
                    })
            }, modifier = Modifier.clickable {
                searchSettingsViewModel.setUseSearchHighlighting(!useSearchHighlighting)
            })
        }
    }

    if (showSearchHistoriesDialog) {
        AlertDialog(
            onDismissRequest = { showSearchHistoriesDialog = false },
            title = { Text(stringResource(R.string.search_histories)) },
            confirmButton = {
                TextButton(onClick = { showSearchHistoriesDialog = false }) {
                    Text(stringResource(R.string.close))
                }
            },
            text = {
                Column(Modifier.verticalScroll(searchHistoriesDialogScrollState)) {
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(stringResource(R.string.all)) },
                        trailingContent = {
                            FilledTonalIconButton(onClick = {
                                searchSettingsViewModel.clearAllSearchHistories()
                            }, enabled = typesWithHistory.isNotEmpty()) {
                                Icon(
                                    Icons.Filled.DeleteForever,
                                    contentDescription = stringResource(R.string.clear)
                                )
                            }
                        })
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(stringResource(R.string.tv)) },
                        trailingContent = {
                            FilledTonalIconButton(onClick = {
                                searchSettingsViewModel.clearSearchHistory(ContentType.Tv)
                            }, enabled = typesWithHistory.contains(ContentType.Tv)) {
                                Icon(
                                    Icons.Filled.DeleteForever,
                                    contentDescription = stringResource(R.string.clear)
                                )
                            }
                        })
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(stringResource(R.string.radio)) },
                        trailingContent = {
                            FilledTonalIconButton(onClick = {
                                searchSettingsViewModel.clearSearchHistory(ContentType.Radio)
                            }, enabled = typesWithHistory.contains(ContentType.Radio)) {
                                Icon(
                                    Icons.Filled.DeleteForever,
                                    contentDescription = stringResource(R.string.clear)
                                )
                            }
                        })
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(stringResource(R.string.movies)) },
                        trailingContent = {
                            FilledTonalIconButton(onClick = {
                                searchSettingsViewModel.clearSearchHistory(ContentType.Movies)
                            }, enabled = typesWithHistory.contains(ContentType.Movies)) {
                                Icon(
                                    Icons.Filled.DeleteForever,
                                    contentDescription = stringResource(R.string.clear)
                                )
                            }
                        })
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(stringResource(R.string.timers)) },
                        trailingContent = {
                            FilledTonalIconButton(onClick = {
                                searchSettingsViewModel.clearSearchHistory(ContentType.Timers)
                            }, enabled = typesWithHistory.contains(ContentType.Timers)) {
                                Icon(
                                    Icons.Filled.DeleteForever,
                                    contentDescription = stringResource(R.string.clear)
                                )
                            }
                        })
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(stringResource(R.string.tv_epg)) },
                        trailingContent = {
                            FilledTonalIconButton(onClick = {
                                searchSettingsViewModel.clearSearchHistory(ContentType.TvEpg)
                            }, enabled = typesWithHistory.contains(ContentType.TvEpg)) {
                                Icon(
                                    Icons.Filled.DeleteForever,
                                    contentDescription = stringResource(R.string.clear)
                                )
                            }
                        })
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(stringResource(R.string.radio_epg)) },
                        trailingContent = {
                            FilledTonalIconButton(onClick = {
                                searchSettingsViewModel.clearSearchHistory(ContentType.RadioEpg)
                            }, enabled = typesWithHistory.contains(ContentType.RadioEpg)) {
                                Icon(
                                    Icons.Filled.DeleteForever,
                                    contentDescription = stringResource(R.string.clear)
                                )
                            }
                        })
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(stringResource(R.string.service_epg)) },
                        trailingContent = {
                            FilledTonalIconButton(onClick = {
                                searchSettingsViewModel.clearSearchHistory(ContentType.ServiceEpg)
                            }, enabled = typesWithHistory.contains(ContentType.ServiceEpg)) {
                                Icon(
                                    Icons.Filled.DeleteForever,
                                    contentDescription = stringResource(R.string.clear)
                                )
                            }
                        })

                }
            })
    }
}