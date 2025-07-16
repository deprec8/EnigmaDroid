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

package io.github.deprec8.enigmadroid.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import io.github.deprec8.enigmadroid.R

@Composable
fun SearchHistory(
    searchHistory: List<String>,
    paddingValues: PaddingValues = calculateSearchTopAppBarContentPaddingValues(),
    onTermSearchClick: (String) -> Unit,
    onTermInsertClick: (String) -> Unit
) {
    if (searchHistory.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .imePadding(),
            contentPadding = paddingValues
        ) {
            items(searchHistory) { searchTerm ->
                ListItem(
                    headlineContent = { Text(searchTerm) },
                    trailingContent = {
                        IconButton(onClick = { onTermInsertClick(searchTerm) }) {
                            Icon(
                                Icons.Default.NorthWest,
                                contentDescription = stringResource(R.string.insert_search_term)
                            )
                        }
                    },
                    leadingContent = {
                        Icon(Icons.Default.History, contentDescription = null)
                    },
                    modifier = Modifier.clickable(onClick = { onTermSearchClick(searchTerm) }),
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .consumeWindowInsets(paddingValues)
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding(), contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.type_something_to_search),
                textAlign = TextAlign.Center
            )
        }
    }
}