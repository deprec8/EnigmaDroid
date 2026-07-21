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

package io.github.deprec8.enigmadroid.ui.components.search

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.source.local.SearchHistoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHistory(
    searchHistory: List<SearchHistoryItem>,
    onSearchQuery: (String) -> Unit,
    onInsertQuery: (String) -> Unit,
    onRemoveItem: (SearchHistoryItem) -> Unit
) {
    if (searchHistory.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            items(searchHistory, key = { item -> item.id }) { item ->
                var showRemoveDialog by rememberSaveable { mutableStateOf(false) }

                ListItem(
                    headlineContent = { Text(item.query) },
                    trailingContent = {
                        TooltipBox(
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(id = R.string.insert))
                                }
                            },
                            state = rememberTooltipState(),
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Above, 4.dp
                            )
                        ) {
                            IconButton(onClick = { onInsertQuery(item.query) }) {
                                Icon(
                                    Icons.Default.NorthWest,
                                    contentDescription = stringResource(R.string.insert)
                                )
                            }
                        }
                    },
                    leadingContent = {
                        Icon(Icons.Default.History, contentDescription = null)
                    },
                    modifier = Modifier
                        .combinedClickable(
                            onClick = { onSearchQuery(item.query) },
                            onLongClick = {
                                showRemoveDialog = true
                            })
                        .animateItem(),
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )

                if (showRemoveDialog) {
                    AlertDialog(onDismissRequest = { showRemoveDialog = false }, dismissButton = {
                        TextButton(onClick = { showRemoveDialog = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }, confirmButton = {
                        TextButton(onClick = {
                            onRemoveItem(item)
                            showRemoveDialog = false
                        }) {
                            Text(stringResource(R.string.remove))
                        }
                    }, text = {
                        Text(stringResource(R.string.remove_query))
                    })
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(), contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.type_something_to_search),
                textAlign = TextAlign.Center
            )
        }
    }
}