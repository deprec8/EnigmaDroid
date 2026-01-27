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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.model.api.timers.Timer
import io.github.deprec8.enigmadroid.utils.TimestampUtils

@Composable
fun TimerLogDialog(timer: Timer, onDismissRequest: () -> Unit) {
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
                            text = TimestampUtils.formatApiTimestampToDate(
                                it.timestamp
                            ) + " " + TimestampUtils.formatApiTimestampToTime(
                                it.timestamp
                            )
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