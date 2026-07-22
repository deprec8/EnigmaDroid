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

package io.github.deprec8.enigmadroid.ui.epg

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.CalendarContract
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.model.MenuItem
import io.github.deprec8.enigmadroid.model.MenuItemGroup
import io.github.deprec8.enigmadroid.model.api.Event
import io.github.deprec8.enigmadroid.ui.components.NoResults
import io.github.deprec8.enigmadroid.ui.components.content.ContentItem
import io.github.deprec8.enigmadroid.utils.TimestampUtils

@Composable
fun EpgContent(
    events: List<Event>,
    paddingValues: PaddingValues,
    showChannelName: Boolean = false,
    onAddTimerForEvent: (Event) -> Unit,
) {
    val context = LocalContext.current
    var showReminderIntentErrorDialog by rememberSaveable {
        mutableStateOf(false)
    }

    fun addReminderForEvent(event: Event) {
        val calendarIntent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(
                CalendarContract.Events.TITLE, event.title
            )
            putExtra(
                CalendarContract.Events.DESCRIPTION, event.shortDescription
            )
            putExtra(
                CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.beginTimestamp * 1000
            )
            putExtra(
                CalendarContract.EXTRA_EVENT_END_TIME,
                (event.beginTimestamp + event.durationInSeconds) * 1000
            )
        }

        try {
            context.startActivity(calendarIntent)
        } catch (_: ActivityNotFoundException) {
            showReminderIntentErrorDialog = true
        }
    }

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
                ContentItem(
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
                                            addReminderForEvent(event)
                                        })
                                )
                            )
                        )
                    } else {
                        null
                    },
                    progress = if (event.beginTimestamp * 1000 <= System.currentTimeMillis()) {
                        ((event.nowTimestamp - event.beginTimestamp).toFloat() / event.durationInSeconds)
                    } else {
                        null
                    },
                    additionalDescription = event.genre
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

    if (showReminderIntentErrorDialog) {
        AlertDialog(onDismissRequest = { showReminderIntentErrorDialog = false }, title = {
            Text(text = stringResource(R.string.unable_to_add_reminder))
        }, text = {
            Text(stringResource(R.string.please_make_sure_that_you_have_at_least_one_calendar_app_installed_on_your_device))
        }, confirmButton = {
            TextButton(onClick = { showReminderIntentErrorDialog = false }) {
                Text(stringResource(R.string.ok))
            }
        })
    }
}