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

package io.github.deprec8.enigmadroid.ui.live.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Dvr
import androidx.compose.material.icons.automirrored.outlined.Dvr
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Cast
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
fun LiveContent(
    events: List<Event>,
    paddingValues: PaddingValues,
    showChannelNumbers: Boolean = true,
    highlightedWords: List<String> = emptyList(),
    onPlayOnDevice: (serviceReference: String) -> Unit,
    onAddTimerForEvent: (event: Event) -> Unit,
    onNavigateToServiceEpg: (serviceReference: String, serviceName: String) -> Unit,
    buildLiveStreamUrl: suspend (serviceReference: String) -> String,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (events.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(310.dp),
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .imePadding(),
            contentPadding = paddingValues
        ) {
            itemsIndexed(events) { index, event ->
                ContentListItem(
                    highlightedWords = highlightedWords,
                    headlineText = event.serviceName,
                    leadingContent = if (showChannelNumbers) {
                        {
                            Text(
                                text = "${index + 1}.",
                                textAlign = TextAlign.Center,
                            )
                        }
                    } else {
                        null
                    },
                    supportingText = event.title,
                    additionalInfo = "${TimestampUtils.formatApiTimestampToTime(event.beginTimestamp)} - " + TimestampUtils.formatApiTimestampToTime(
                        event.beginTimestamp + event.durationInSeconds
                    ),
                    menuItemGroups = listOf(
                        MenuItemGroup(
                            listOf(
                                MenuItem(
                                    text = stringResource(R.string.stream),
                                    outlinedIcon = Icons.Outlined.Cast,
                                    filledIcon = Icons.Filled.Cast,
                                    action = {
                                        scope.launch {
                                            IntentUtils.playMedia(
                                                context,
                                                buildLiveStreamUrl(event.serviceReference),
                                                event.serviceName
                                            )
                                        }
                                    }), MenuItem(
                                    text = stringResource(R.string.switch_channel),
                                    outlinedIcon = Icons.Outlined.PlayArrow,
                                    filledIcon = Icons.Filled.PlayArrow,
                                    action = {
                                        onPlayOnDevice(event.serviceReference)
                                    }), MenuItem(
                                    text = stringResource(R.string.record),
                                    outlinedIcon = Icons.Outlined.Videocam,
                                    filledIcon = Icons.Filled.Videocam,
                                    action = {
                                        onAddTimerForEvent(event)

                                    }), MenuItem(
                                    text = stringResource(R.string.view_epg),
                                    outlinedIcon = Icons.AutoMirrored.Outlined.Dvr,
                                    filledIcon = Icons.AutoMirrored.Filled.Dvr,
                                    action = {
                                        onNavigateToServiceEpg(
                                            event.serviceReference, event.serviceName
                                        )
                                    })
                            )
                        )
                    ),
                    progress = ((event.nowTimestamp - event.beginTimestamp).toFloat() / event.durationInSeconds),
                    shortDescription = event.shortDescription,
                    longDescription = event.longDescription
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