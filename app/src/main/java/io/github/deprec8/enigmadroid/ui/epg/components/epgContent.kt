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

package io.github.deprec8.enigmadroid.ui.epg.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.model.api.events.Event
import io.github.deprec8.enigmadroid.model.menu.MenuItem
import io.github.deprec8.enigmadroid.model.menu.MenuItemGroup
import io.github.deprec8.enigmadroid.ui.components.NoResults
import io.github.deprec8.enigmadroid.ui.components.content.ContentListItem
import io.github.deprec8.enigmadroid.utils.IntentUtils
import io.github.deprec8.enigmadroid.utils.TimestampUtils
import kotlinx.coroutines.launch

@Composable
fun EpgContent(
    events: List<Event>,
    paddingValues: PaddingValues,
    showChannelName: Boolean = false,
    highlightedWords: List<String> = emptyList(),
    onAddTimerForEvent: (Event) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (events.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(310.dp),
            Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .imePadding(),
            contentPadding = paddingValues
        ) {
            items(events) { event ->
                ContentListItem(
                    highlightedWords = highlightedWords,
                    headlineText = event.title,
                    supportingText = TimestampUtils.formatApiTimestampToDate(event.beginTimestamp),
                    overlineText = if (event.beginTimestamp * 1000 <= System.currentTimeMillis()) {
                        stringResource(R.string.now)
                    } else {
                        TimestampUtils.formatApiTimestampToTime(
                            event.beginTimestamp
                        )
                    } + if (showChannelName) {
                        " - ${event.serviceName}"
                    } else {
                        ""
                    },
                    shortDescription = event.shortDescription,
                    longDescription = event.longDescription,
                    menuItemGroups = if (event.beginTimestamp * 1000 > System.currentTimeMillis()) {
                        listOf(
                            MenuItemGroup(
                                listOf(
                                    MenuItem(
                                        text = stringResource(R.string.add_timer),
                                        outlinedIcon = Icons.Outlined.Timer,
                                        filledIcon = Icons.Filled.Timer,
                                        action = {
                                            onAddTimerForEvent(event)
                                        }), MenuItem(
                                        text = stringResource(R.string.add_reminder),
                                        outlinedIcon = Icons.Outlined.AddAlert,
                                        filledIcon = Icons.Filled.AddAlert,
                                        action = {
                                            scope.launch {
                                                IntentUtils.addReminder(context, event)
                                            }
                                        })
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