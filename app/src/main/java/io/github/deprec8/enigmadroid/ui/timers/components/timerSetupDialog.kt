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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AutoAwesomeMosaic
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TimePickerDialogDefaults
import androidx.compose.material3.TimePickerDialogDefaults.MinHeightForTimePicker
import androidx.compose.material3.TimePickerDisplayMode
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.EntryType
import io.github.deprec8.enigmadroid.model.api.timers.Timer
import io.github.deprec8.enigmadroid.model.api.timers.services.Service
import io.github.deprec8.enigmadroid.model.api.timers.services.ServiceBatch
import io.github.deprec8.enigmadroid.model.api.timers.services.ServiceBatchSet
import io.github.deprec8.enigmadroid.ui.components.dialogs.AdaptiveDialog
import io.github.deprec8.enigmadroid.utils.TimestampUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerSetupDialog(
    oldTimer: Timer? = null,
    serviceBatchSet: ServiceBatchSet?,
    onDismissRequest: () -> Unit,
    onSaveRequest: (newTimer: Timer, oldTimer: Timer?) -> Unit,
) {
    val titleState = rememberTextFieldState(oldTimer?.title ?: "")
    val shortDescriptionState = rememberTextFieldState(oldTimer?.shortDescription ?: "")

    var disabled by rememberSaveable { mutableIntStateOf(oldTimer?.disabled ?: 0) }
    var justPlay by rememberSaveable { mutableIntStateOf(oldTimer?.justPlay ?: 0) }
    var beginTimestamp by rememberSaveable {
        mutableLongStateOf(
            oldTimer?.beginTimestamp?.times(1000) ?: System.currentTimeMillis()
        )
    }
    var endTimestamp by rememberSaveable {
        mutableLongStateOf(
            oldTimer?.endTimestamp?.times(1000) ?: (System.currentTimeMillis() + 3600000)
        )
    }
    var afterevent by rememberSaveable { mutableIntStateOf(oldTimer?.afterEvent ?: 3) }
    var serviceReference by rememberSaveable { mutableStateOf(oldTimer?.serviceReference ?: "") }
    var repeated by rememberSaveable { mutableIntStateOf(oldTimer?.repeated ?: 0) }
    var alwaysZap by rememberSaveable { mutableIntStateOf(oldTimer?.alwaysZap ?: 0) }

    var showAftereventMenu by rememberSaveable { mutableStateOf(false) }
    var showBeginDatePicker by rememberSaveable { mutableStateOf(false) }
    var showEndDatePicker by rememberSaveable { mutableStateOf(false) }
    var showBeginTimePicker by rememberSaveable { mutableStateOf(false) }
    var showEndTimePicker by rememberSaveable { mutableStateOf(false) }
    var showServicePicker by rememberSaveable { mutableStateOf(false) }

    val toggleScrollState = rememberScrollState()

    val days = listOf(
        Pair(0b0000001, R.string.monday),
        Pair(0b0000010, R.string.tuesday),
        Pair(0b0000100, R.string.wednesday),
        Pair(0b0001000, R.string.thursday),
        Pair(0b0010000, R.string.friday),
        Pair(0b0100000, R.string.saturday),
        Pair(0b1000000, R.string.sunday)
    )

    fun isEverythingValid(): Boolean {
        return if (oldTimer == null) {
            titleState.text.isNotBlank() && serviceReference.isNotBlank() && beginTimestamp < endTimestamp
        } else {
            titleState.text.isNotBlank() && serviceReference.isNotBlank() && beginTimestamp < endTimestamp && (oldTimer.serviceReference != serviceReference || oldTimer.title != titleState.text || oldTimer.shortDescription != shortDescriptionState.text || oldTimer.disabled != disabled || oldTimer.justPlay != justPlay || oldTimer.beginTimestamp != (beginTimestamp / 1000) || oldTimer.endTimestamp != (endTimestamp / 1000) || oldTimer.afterEvent != afterevent || oldTimer.repeated != repeated || oldTimer.alwaysZap != alwaysZap)
        }
    }

    AdaptiveDialog(
        onDismissRequest = {
            onDismissRequest()
        }, title = if (oldTimer == null) {
            stringResource(R.string.add_timer)
        } else {
            stringResource(R.string.edit_timer)
        }, actionButton = {
            TextButton(
                enabled = isEverythingValid(), onClick = {
                    onSaveRequest(
                        Timer(
                            serviceReference = serviceReference,
                            beginTimestamp = beginTimestamp / 1000,
                            endTimestamp = endTimestamp / 1000,
                            justPlay = justPlay,
                            afterEvent = afterevent,
                            disabled = disabled,
                            shortDescription = shortDescriptionState.text.toString(),
                            title = titleState.text.toString(),
                            repeated = repeated,
                            alwaysZap = alwaysZap,
                        ), oldTimer
                    )
                }) {
                Text(
                    text = if (oldTimer == null) {
                        stringResource(R.string.create)
                    } else {
                        stringResource(R.string.save)
                    }
                )
            }
        }, content = { isContentScrollable ->
            Column {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text(text = stringResource(R.string.service)) },
                    supportingContent = {
                        if (serviceBatchSet != null) {
                            Text(text = serviceBatchSet.serviceBatches.flatMap { it.services }
                                .firstOrNull {
                                    it.serviceReference == serviceReference
                                }?.serviceName ?: serviceReference.ifBlank {
                                stringResource(R.string.no_service_selected)
                            })
                        } else {
                            LinearProgressIndicator(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(serviceBatchSet != null) { showServicePicker = true })
                Spacer(Modifier.size(8.dp))
                OutlinedTextField(
                    state = titleState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    label = { Text(text = stringResource(R.string.title)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(Modifier.size(8.dp))
                OutlinedTextField(
                    state = shortDescriptionState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    label = { Text(text = stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
                )
                Spacer(Modifier.size(8.dp))
                Row {
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(text = stringResource(R.string.begin_date)) },
                        supportingContent = {
                            Text(
                                text = TimestampUtils.formatTimestampToDate(beginTimestamp)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showBeginDatePicker = true })
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(text = stringResource(R.string.begin_time)) },
                        supportingContent = {
                            Text(
                                text = TimestampUtils.formatTimestampToTime(beginTimestamp)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showBeginTimePicker = true })
                }
                Row {
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(text = stringResource(R.string.end_date)) },
                        supportingContent = {
                            Text(
                                text = TimestampUtils.formatTimestampToDate(endTimestamp)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showEndDatePicker = true })
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(text = stringResource(R.string.end_time)) },
                        supportingContent = {
                            Text(
                                text = TimestampUtils.formatTimestampToTime(endTimestamp)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showEndTimePicker = true })
                }
                Spacer(Modifier.size(8.dp))
                Row(
                    Modifier.horizontalScroll(toggleScrollState)
                ) {
                    FilterChip(selected = disabled == 0, onClick = {
                        disabled = if (disabled == 0) {
                            1
                        } else {
                            0
                        }
                    }, label = {
                        Text(
                            text = stringResource(R.string.enabled)
                        )
                    }, leadingIcon = {
                        AnimatedVisibility(disabled == 0) {
                            Icon(
                                Icons.Default.Check, contentDescription = null, Modifier.size(
                                    FilterChipDefaults.IconSize
                                )
                            )
                        }
                    })
                    FilterChip(
                        selected = justPlay == 1, onClick = {
                            justPlay = if (justPlay == 0) {
                                1
                            } else {
                                0
                            }
                        }, label = {
                            Text(
                                text = stringResource(R.string.justplay)
                            )
                        }, leadingIcon = {
                            AnimatedVisibility(justPlay == 1) {
                                Icon(
                                    Icons.Default.Check, contentDescription = null, Modifier.size(
                                        FilterChipDefaults.IconSize
                                    )
                                )
                            }
                        }, modifier = Modifier.padding(start = 8.dp)
                    )
                    FilterChip(
                        selected = alwaysZap == 1, onClick = {
                            alwaysZap = if (alwaysZap == 0) {
                                1
                            } else {
                                0
                            }
                        }, label = {
                            Text(
                                text = stringResource(R.string.switch_channel)
                            )
                        }, leadingIcon = {
                            AnimatedVisibility(alwaysZap == 1) {
                                Icon(
                                    Icons.Default.Check, contentDescription = null, Modifier.size(
                                        FilterChipDefaults.IconSize
                                    )
                                )
                            }
                        }, modifier = Modifier.padding(start = 8.dp)
                    )
                }
                ExposedDropdownMenuBox(
                    expanded = showAftereventMenu, onExpandedChange = {
                        showAftereventMenu = it
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = when (afterevent) {
                            2 -> {
                                stringResource(R.string.shutdown)
                            }
                            1 -> {
                                stringResource(R.string.standby)
                            }
                            else -> {
                                stringResource(R.string.automatic)
                            }
                        },
                        onValueChange = { },
                        readOnly = true,
                        singleLine = true,
                        label = { Text(stringResource(R.string.after_event)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showAftereventMenu) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = showAftereventMenu,
                        scrollState = rememberScrollState(),
                        onDismissRequest = { showAftereventMenu = false },
                        containerColor = if (isContentScrollable) {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        } else {
                            MenuDefaults.containerColor
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.automatic)) },
                            onClick = {
                                afterevent = 3
                                showAftereventMenu = false
                            })
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.shutdown)) },
                            onClick = {
                                afterevent = 2
                                showAftereventMenu = false
                            })
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.standby)) },
                            onClick = {
                                afterevent = 1
                                showAftereventMenu = false
                            })
                    }

                }
                Spacer(Modifier.size(8.dp))
                Text(stringResource(R.string.repeats_on))
                Spacer(Modifier.size(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    days.forEach { (bitmask, stringResId) ->
                        FilterChip(
                            label = { Text(text = stringResource(stringResId)) },
                            selected = (repeated and bitmask) != 0,
                            onClick = {
                                repeated = if ((repeated and bitmask) == 0) {
                                    repeated or bitmask
                                } else {
                                    repeated and bitmask.inv()
                                }
                            },
                            leadingIcon = {
                                AnimatedVisibility(visible = (repeated and bitmask) != 0) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            },
                        )
                    }
                }

                if (showServicePicker) {
                    ServicePickerDialog(
                        serviceBatchSet = serviceBatchSet,
                        onDismissRequest = { showServicePicker = false },
                        onServiceClicked = {
                            serviceReference = it.serviceReference
                        },
                        currentServiceReference = serviceReference
                    )
                }

                if (showBeginDatePicker) {
                    SetupDatePickerDialog(timestamp = beginTimestamp, onDismissRequest = {
                        showBeginDatePicker = false
                    }, onSaveRequest = {
                        beginTimestamp = it
                        showBeginDatePicker = false
                    })
                }

                if (showEndDatePicker) {
                    SetupDatePickerDialog(timestamp = endTimestamp, onDismissRequest = {
                        showEndDatePicker = false
                    }, onSaveRequest = {
                        endTimestamp = it
                        showEndDatePicker = false
                    })
                }

                if (showBeginTimePicker) {
                    SetupTimePickerDialog(
                        timestamp = beginTimestamp,
                        onDismissRequest = { showBeginTimePicker = false },
                        onSaveRequest = {
                            beginTimestamp = it
                            showBeginTimePicker = false
                        })
                }

                if (showEndTimePicker) {
                    SetupTimePickerDialog(
                        timestamp = endTimestamp,
                        onDismissRequest = { showEndTimePicker = false },
                        onSaveRequest = {
                            endTimestamp = it
                            showEndTimePicker = false
                        })
                }
            }
        })
}

@Composable
private fun ServicePickerDialog(
    serviceBatchSet: ServiceBatchSet?,
    currentServiceReference: String? = null,
    onDismissRequest: () -> Unit,
    onServiceClicked: (service: Service) -> Unit
) {
    var currentServiceBatch by remember {
        mutableStateOf<ServiceBatch?>(null)
    }

    AlertDialog(onDismissRequest = {
        onDismissRequest()
    }, text = {
        if (currentServiceBatch != null) {
            Column {
                ListItem(
                    headlineContent = { Text(currentServiceBatch?.name ?: "") },
                    leadingContent = {
                        IconButton(onClick = {
                            currentServiceBatch = null
                        }) {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                stringResource(R.string.go_back)
                            )
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                HorizontalDivider()
                LazyColumn {
                    items(currentServiceBatch?.services ?: emptyList()) { service ->
                        when (service.type) {
                            EntryType.CHANNEL -> {
                                ListItem(
                                    headlineContent = {
                                        Text(service.serviceName)
                                    },
                                    leadingContent = {
                                        Text(
                                            text = "${service.displayIndex}.",
                                            textAlign = TextAlign.Center,
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        onServiceClicked(service)
                                    },
                                    trailingContent = {
                                        AnimatedVisibility(
                                            visible = currentServiceReference == service.serviceReference,
                                            enter = expandIn(expandFrom = Alignment.Center) + fadeIn(),
                                            exit = shrinkOut(shrinkTowards = Alignment.Center) + fadeOut()
                                        ) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = stringResource(R.string.current_service)
                                            )
                                        }
                                    },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                )
                            }
                            EntryType.MARKER  -> {
                                Column {
                                    ListItem(
                                        headlineContent = {
                                            Text(service.serviceName)
                                        },
                                        leadingContent = {
                                            Icon(
                                                Icons.Outlined.Bookmark,
                                                stringResource(R.string.marker)
                                            )
                                        },
                                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                    )
                                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                                }
                            }
                            EntryType.DIRECTORY -> {
                                Column {
                                    ListItem(
                                        headlineContent = {
                                            Text(service.serviceName)
                                        },
                                        leadingContent = {
                                            Icon(
                                                Icons.Outlined.Folder,
                                                stringResource(R.string.directory)
                                            )
                                        },
                                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                    )
                                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                                }
                            }
                            EntryType.GROUP   -> {
                                Column {
                                    ListItem(
                                        headlineContent = {
                                            Text(service.serviceName)
                                        },
                                        leadingContent = {
                                            Icon(
                                                Icons.Outlined.AutoAwesomeMosaic,
                                                stringResource(R.string.group)
                                            )
                                        },
                                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                    )
                                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                                }
                            }
                            else              -> {}
                        }
                    }
                }
            }
        } else if (serviceBatchSet != null) {
            LazyColumn {
                items(serviceBatchSet.serviceBatches) { serviceBatch ->
                    ListItem(
                        headlineContent = {
                            Text(serviceBatch.name)
                        },
                        modifier = Modifier.clickable { currentServiceBatch = serviceBatch },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        leadingContent = {
                            Icon(
                                Icons.Outlined.Folder, stringResource(R.string.directory)
                            )
                        },
                        trailingContent = {
                            Icon(Icons.AutoMirrored.Filled.ArrowRight, contentDescription = null)
                        })
                }
            }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                Text(
                    text = stringResource(R.string.no_results), textAlign = TextAlign.Center
                )
            }
        }
    }, confirmButton = {
        TextButton(onClick = { onDismissRequest() }) {
            Text(stringResource(R.string.close))
        }
    }, title = {
        Text(
            stringResource(R.string.choose_service)
        )
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetupTimePickerDialog(
    timestamp: Long, onDismissRequest: () -> Unit, onSaveRequest: (Long) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = TimestampUtils.millisToHourInt(timestamp),
        initialMinute = TimestampUtils.millisToMinuteInt(timestamp)
    )
    var displayMode by remember { mutableStateOf(TimePickerDisplayMode.Picker) }

    TimePickerDialog(
        onDismissRequest = { onDismissRequest() },
        title = { TimePickerDialogDefaults.Title(displayMode = displayMode) },
        confirmButton = {
            TextButton(onClick = {
                onSaveRequest(
                    TimestampUtils.combineTimeDate(
                        timestamp, TimestampUtils.getMillisFromTimeString(
                            state.hour.toString() + ":" + state.minute.toString()
                        )
                    )
                )
            }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(stringResource(R.string.cancel))
            }
        },
        modeToggleButton = {
            if (LocalWindowInfo.current.containerSize.height.dp > MinHeightForTimePicker) {
                TimePickerDialogDefaults.DisplayModeToggle(
                    onDisplayModeChange = {
                        displayMode = if (displayMode == TimePickerDisplayMode.Picker) {
                            TimePickerDisplayMode.Input
                        } else {
                            TimePickerDisplayMode.Picker
                        }
                    },
                    displayMode = displayMode,
                )
            }
        }) {
        if (displayMode == TimePickerDisplayMode.Picker && LocalWindowInfo.current.containerSize.height.dp > MinHeightForTimePicker) {
            TimePicker(state = state)
        } else {
            TimeInput(state = state)
        }
    }
}

@Composable
private fun SetupDatePickerDialog(
    timestamp: Long, onDismissRequest: () -> Unit, onSaveRequest: (Long) -> Unit
) {
    val state = rememberDatePickerState(initialSelectedDateMillis = timestamp)
    val dateScrollState = rememberScrollState()

    DatePickerDialog(onDismissRequest = {
        onDismissRequest()
    }, confirmButton = {
        TextButton(onClick = {
            onSaveRequest(
                TimestampUtils.combineDateTime(
                    timestamp, state.selectedDateMillis ?: timestamp
                )
            )
        }) { Text(stringResource(R.string.ok)) }

    }, dismissButton = {
        TextButton(
            onClick = {
                onDismissRequest()
            },
        ) { Text(stringResource(R.string.cancel)) }
    }) {
        DatePicker(
            modifier = Modifier.verticalScroll(dateScrollState),
            state = state,
        )
    }
}