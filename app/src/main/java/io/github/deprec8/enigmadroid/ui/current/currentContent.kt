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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.model.api.CurrentInfo
import io.github.deprec8.enigmadroid.ui.components.isSmallScreenLayout
import io.github.deprec8.enigmadroid.utils.IntentUtils
import io.github.deprec8.enigmadroid.utils.TimestampUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentContent(
    modifier: Modifier = Modifier,
    currentEventInfo: CurrentInfo,
    paddingValues: PaddingValues,
    onBuildLiveStreamUrl: suspend (String) -> String,
    onNavigateToServiceEpg: (String, String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val isExpandedScreenLayout = ! isSmallScreenLayout()

    if (currentEventInfo.info.result == true) {
        Column(
            modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .verticalScroll(scrollState)
                .padding(paddingValues)
        ) {
            if (isExpandedScreenLayout) {
                Row {
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.channel)) },
                        supportingContent = {
                            Text(text = currentEventInfo.now.serviceName)
                        },
                        modifier = Modifier.fillMaxWidth(0.5f)
                    )
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.provider)) },
                        supportingContent = {
                            Text(text = currentEventInfo.now.provider)
                        },
                        modifier = Modifier.fillMaxWidth(1f)
                    )
                }
            } else {
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.channel)) },
                    supportingContent = {
                        Text(text = currentEventInfo.now.serviceName)
                    })
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.provider)) },
                    supportingContent = {
                        Text(text = currentEventInfo.now.provider)
                    })
            }

            if (isExpandedScreenLayout) {
                Row {
                    Column(modifier = Modifier.fillMaxWidth(0.5f)) {
                        ListItem(
                            overlineContent = {
                                Text(
                                    text = stringResource(R.string.now) + " - " + stringResource(
                                        R.string.until, TimestampUtils.formatApiTimestampToTime(
                                            currentEventInfo.now.beginTimestamp + currentEventInfo.now.durationInSeconds
                                        )
                                    )
                                )
                            },
                            headlineContent = { Text(text = currentEventInfo.now.title) },
                            supportingContent = if (currentEventInfo.next.shortDescription.isNotBlank()) {
                                {
                                    Text(
                                        text = currentEventInfo.now.shortDescription
                                    )
                                }
                            } else {
                                null
                            })
                        LinearProgressIndicator(
                            progress = {
                                if (currentEventInfo.now.durationInSeconds > 0) {
                                    ((currentEventInfo.now.nowTimestamp - currentEventInfo.now.beginTimestamp).toFloat() / currentEventInfo.now.durationInSeconds)
                                } else {
                                    0f
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 16.dp, end = 16.dp
                                ),
                            strokeCap = StrokeCap.Round,
                        )
                    }
                    ListItem(
                        overlineContent = {
                            Text(
                                text = stringResource(R.string.next) + " - " + stringResource(
                                    R.string.starting_at, TimestampUtils.formatApiTimestampToTime(
                                        currentEventInfo.next.beginTimestamp
                                    )
                                )
                            )
                        },
                        headlineContent = { Text(text = currentEventInfo.next.title) },
                        supportingContent = if (currentEventInfo.next.shortDescription.isNotBlank()) {
                            {
                                Text(
                                    text = currentEventInfo.next.shortDescription
                                )
                            }
                        } else {
                            null
                        },
                        modifier = Modifier.fillMaxWidth(1f))
                }
            } else {
                ListItem(
                    overlineContent = {
                        Text(
                            text = stringResource(R.string.now) + " - " + stringResource(
                                R.string.until, TimestampUtils.formatApiTimestampToTime(
                                    currentEventInfo.now.beginTimestamp + currentEventInfo.now.durationInSeconds
                                )
                            )
                        )
                    },
                    headlineContent = { Text(text = currentEventInfo.now.title) },
                    supportingContent = if (currentEventInfo.next.shortDescription.isNotBlank()) {
                        {
                            Text(
                                text = currentEventInfo.now.shortDescription
                            )
                        }
                    } else {
                        null
                    })
                LinearProgressIndicator(
                    progress = {
                        if (currentEventInfo.now.durationInSeconds > 0) {
                            ((currentEventInfo.now.nowTimestamp - currentEventInfo.now.beginTimestamp).toFloat() / currentEventInfo.now.durationInSeconds)
                        } else {
                            0f
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp, end = 16.dp
                        ),
                    strokeCap = StrokeCap.Round,
                )
                Spacer(Modifier.size(8.dp))
                ListItem(
                    overlineContent = {
                        Text(
                            text = stringResource(R.string.next) + " - " + stringResource(
                                R.string.starting_at, TimestampUtils.formatApiTimestampToTime(
                                    currentEventInfo.next.beginTimestamp
                                )
                            )
                        )
                    },
                    headlineContent = { Text(text = currentEventInfo.next.title) },
                    supportingContent = if (currentEventInfo.next.shortDescription.isNotBlank()) {
                        {
                            Text(
                                text = currentEventInfo.next.shortDescription
                            )
                        }
                    } else {
                        null
                    })
            }
            Spacer(Modifier.size(16.dp))
            if (isExpandedScreenLayout) {
                Row {
                    FilledTonalButton(
                        onClick = {
                            scope.launch {
                                IntentUtils.playMedia(
                                    context,
                                    onBuildLiveStreamUrl(currentEventInfo.now.serviceReference),
                                    currentEventInfo.now.serviceName
                                )
                            }
                        }, Modifier
                            .fillMaxWidth(0.5f)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = stringResource(
                                R.string.stream_service, currentEventInfo.now.serviceName
                            )
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            onNavigateToServiceEpg(
                                currentEventInfo.now.serviceReference,
                                currentEventInfo.now.serviceName
                            )
                        }, Modifier
                            .fillMaxWidth(1f)
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(text = stringResource(R.string.view_epg))
                    }
                }
            } else {
                FilledTonalButton(
                    onClick = {
                        scope.launch {
                            IntentUtils.playMedia(
                                context,
                                onBuildLiveStreamUrl(currentEventInfo.now.serviceReference),
                                currentEventInfo.now.serviceName
                            )
                        }
                    }, Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = stringResource(
                            R.string.stream_service, currentEventInfo.now.serviceName
                        )
                    )
                }
                Spacer(Modifier.size(16.dp))
                OutlinedButton(
                    onClick = {
                        onNavigateToServiceEpg(
                            currentEventInfo.now.serviceReference, currentEventInfo.now.serviceName
                        )
                    }, Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = stringResource(
                            R.string.view_epg_from, currentEventInfo.now.serviceName
                        )
                    )
                }
            }
            Spacer(Modifier.size(16.dp))
        }
    } else {
        Box(
            modifier = Modifier
                .consumeWindowInsets(
                    paddingValues
                )
                .fillMaxSize()
                .padding(paddingValues), contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.device_is_not_playing_any_channel),
                textAlign = TextAlign.Center
            )
        }
    }

}