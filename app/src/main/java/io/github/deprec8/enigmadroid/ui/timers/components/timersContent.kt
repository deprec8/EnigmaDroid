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

package io.github.deprec8.enigmadroid.ui.timers.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.TimerOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.TimerState
import io.github.deprec8.enigmadroid.model.api.timers.Timer
import io.github.deprec8.enigmadroid.model.api.timers.services.ServiceBatchSet
import io.github.deprec8.enigmadroid.model.menu.MenuItem
import io.github.deprec8.enigmadroid.model.menu.MenuItemGroup
import io.github.deprec8.enigmadroid.ui.components.NoResults
import io.github.deprec8.enigmadroid.ui.components.content.ContentListItem
import io.github.deprec8.enigmadroid.ui.components.dialogs.ConfirmDeleteDialog
import io.github.deprec8.enigmadroid.utils.TimestampUtils

@Composable
fun TimersContent(
    timers: List<Timer>,
    paddingValues: PaddingValues,
    highlightedWords: List<String> = emptyList(),
    onToggleTimerStatus: (Timer) -> Unit,
    onEditTimer: (Timer, Timer) -> Unit,
    onDeleteTimer: (Timer) -> Unit,
    serviceBatchSet: ServiceBatchSet?
) {
    if (timers.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(310.dp),
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .imePadding()

        ) {
            items(timers) { timer ->
                var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
                var showEditDialog by rememberSaveable { mutableStateOf(false) }
                var showLogDialog by rememberSaveable { mutableStateOf(false) }

                ContentListItem(
                    highlightedWords = highlightedWords,
                    headlineText = timer.title,
                    overlineText = "${timer.serviceName} - ${timer.getState()}${
                        if (timer.cancelled) {
                            " - " + stringResource(
                                R.string.cancelled
                            )
                        } else {
                            ""
                        }
                    }",
                    leadingContent = {
                        TimerStateIcon(timer)
                    },
                    supportingText = TimestampUtils.formatApiTimestampToDateTime(timer.beginTimestamp) + " - " + TimestampUtils.formatApiTimestampToDateTime(
                        timer.endTimestamp
                    ),
                    shortDescription = timer.shortDescription,
                    longDescription = timer.descriptionextended,
                    menuItemGroups = if (timer.logEntries.isNotEmpty()) {
                        listOf(
                            MenuItemGroup(
                                listOf(
                                    MenuItem(
                                        text = stringResource(R.string.view_log),
                                        outlinedIcon = Icons.AutoMirrored.Outlined.List,
                                        filledIcon = Icons.AutoMirrored.Filled.List,
                                        action = { showLogDialog = true }),
                                )
                            )
                        )
                    } else {
                        emptyList()
                    },
                    additionalDescription = timer.directoryName,
                    editMenuItemGroup = MenuItemGroup(
                        listOf(
                            MenuItem(
                                text = if (timer.disabled == 1) {
                                    stringResource(R.string.enable)
                                } else {
                                    stringResource(R.string.disable)
                                }, outlinedIcon = if (timer.disabled == 1) {
                                    Icons.Outlined.Timer
                                } else {
                                    Icons.Outlined.TimerOff
                                }, filledIcon = if (timer.disabled == 1) {
                                    Icons.Filled.Timer
                                } else {
                                    Icons.Filled.TimerOff
                                }, action = { onToggleTimerStatus(timer) }), MenuItem(
                                text = stringResource(R.string.edit),
                                outlinedIcon = Icons.Outlined.Edit,
                                filledIcon = Icons.Filled.Edit,
                                action = { showEditDialog = true }), MenuItem(
                                text = stringResource(R.string.delete),
                                outlinedIcon = Icons.Outlined.Delete,
                                filledIcon = Icons.Filled.Delete,
                                action = { showDeleteDialog = true })
                        )
                    )
                )

                if (showLogDialog) {
                    TimerLogDialog(timer) { showLogDialog = false }
                }

                if (showDeleteDialog) {
                    ConfirmDeleteDialog(
                        title = stringResource(R.string.delete_timer),
                        text = stringResource(R.string.delete_timer_warning),
                        onDismissRequest = { showDeleteDialog = false },
                        onConfirmRequest = {
                            onDeleteTimer(timer)
                            showDeleteDialog = false
                        })
                }
                if (showEditDialog) {
                    TimerSetupDialog(
                        onDismissRequest = { showEditDialog = false },
                        oldTimer = timer,
                        onSaveRequest = { newTimer, oldTimer ->
                            if (oldTimer != null) {
                                onEditTimer(oldTimer, newTimer)
                            }
                            showEditDialog = false
                        },
                        serviceBatchSet = serviceBatchSet,
                    )
                }
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

@Composable
private fun Timer.getState(): String {
    return when (this.state + this.disabled) {
        TimerState.WAITING.id  -> stringResource(R.string.waiting)
        TimerState.PREPARED.id -> stringResource(R.string.prepared)
        TimerState.RUNNING.id  -> stringResource(R.string.running)
        TimerState.ENDED.id    -> stringResource(R.string.ended)
        TimerState.DISABLED.id -> stringResource(R.string.disabled)
        else                   -> {
            stringResource(R.string.unknown)
        }
    }
}

@Composable
private fun TimerLogDialog(timer: Timer, onDismissRequest: () -> Unit) {
    AlertDialog(onDismissRequest = {
        onDismissRequest()
    }, title = { Text(text = stringResource(R.string.log_entries)) }, text = {
        LazyColumn {
            items(timer.logEntries) {
                ListItem(
                    overlineContent = {
                        Text(stringResource(R.string.code, it.code))
                    },
                    headlineContent = {
                        Text(
                            text = TimestampUtils.formatApiTimestampToDateTime(it.timestamp)
                        )
                    },
                    supportingContent = {
                        Text(text = it.message)
                    },
                    colors = ListItemDefaults.colors(containerColor = AlertDialogDefaults.containerColor)
                )
            }
        }
    }, icon = {
        Icon(
            Icons.AutoMirrored.Outlined.List, contentDescription = null
        )
    }, confirmButton = {
        TextButton(onClick = {
            onDismissRequest()
        }) { Text(stringResource(R.string.close)) }
    })
}