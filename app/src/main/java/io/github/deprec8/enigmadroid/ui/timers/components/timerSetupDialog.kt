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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.model.api.timers.Timer
import io.github.deprec8.enigmadroid.model.api.timers.services.ServiceBatch
import io.github.deprec8.enigmadroid.ui.components.AdaptiveDialog
import io.github.deprec8.enigmadroid.utils.TimestampUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerSetupDialog(
    oldTimer: Timer? = null,
    onDismissRequest: () -> Unit,
    onSaveRequest: (newTimer: Timer, oldTimer: Timer?) -> Unit,
    services: List<ServiceBatch>,
) {
    val titleState = rememberTextFieldState("")
    val shortDescriptionState = rememberTextFieldState("")

    var disabled by rememberSaveable { mutableIntStateOf(0) }
    var justPlay by rememberSaveable { mutableIntStateOf(0) }
    var beginTimestamp by rememberSaveable { mutableLongStateOf(0L) }
    var endTimestamp by rememberSaveable { mutableLongStateOf(0L) }
    var afterevent by rememberSaveable { mutableIntStateOf(3) }
    var serviceReference by rememberSaveable { mutableStateOf("") }
    var repeated by rememberSaveable { mutableIntStateOf(0) }
    var alwaysZap by rememberSaveable { mutableIntStateOf(0) }

    var showAftereventMenu by rememberSaveable { mutableStateOf(false) }
    var showServicesMenu by rememberSaveable { mutableStateOf(false) }
    var showBeginDatePicker by rememberSaveable { mutableStateOf(false) }
    var showEndDatePicker by rememberSaveable { mutableStateOf(false) }
    var showBeginTimePicker by rememberSaveable { mutableStateOf(false) }
    var showEndTimePicker by rememberSaveable { mutableStateOf(false) }

    val beginTimeState = rememberTimePickerState()
    val endTimeState = rememberTimePickerState()
    val beginDateState = rememberDatePickerState()
    val endDateState = rememberDatePickerState()

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

    LaunchedEffect(Unit) {
        if (oldTimer != null) {
            titleState.setTextAndPlaceCursorAtEnd(oldTimer.title)
            serviceReference = oldTimer.serviceReference
            shortDescriptionState.setTextAndPlaceCursorAtEnd(oldTimer.shortDescription)
            disabled = oldTimer.disabled
            justPlay = oldTimer.justPlay
            beginTimestamp = oldTimer.beginTimestamp * 1000
            endTimestamp = oldTimer.endTimestamp * 1000
            afterevent = oldTimer.afterEvent
            repeated = oldTimer.repeated
            alwaysZap = oldTimer.alwaysZap
        } else {
            beginTimestamp = System.currentTimeMillis()
            endTimestamp = System.currentTimeMillis() + 3600000
        }

        beginTimeState.hour = TimestampUtils.millisToHourInt(beginTimestamp)
        beginTimeState.minute = TimestampUtils.millisToMinuteInt(beginTimestamp)
        endTimeState.hour = TimestampUtils.millisToHourInt(endTimestamp)
        endTimeState.minute = TimestampUtils.millisToMinuteInt(endTimestamp)
        beginDateState.selectedDateMillis = beginTimestamp
        endDateState.selectedDateMillis = endTimestamp
    }

    fun reset() {
        titleState.clearText()
        serviceReference = ""
        shortDescriptionState.clearText()
        disabled = 0
        justPlay = 0
        beginTimestamp = 0L
        endTimestamp = 0L
        afterevent = 0
        repeated = 0
        alwaysZap = 0
    }

    fun isEverythingValid(): Boolean {
        return if (oldTimer == null) {
            titleState.text.toString() != "" && serviceReference != "" && beginTimestamp / 1000 < endTimestamp / 1000
        } else {
            titleState.text.toString() != "" && serviceReference != "" && beginTimestamp < endTimestamp && (oldTimer.serviceReference != serviceReference || oldTimer.title != titleState.text.toString() || oldTimer.shortDescription != shortDescriptionState.text.toString() || oldTimer.disabled != disabled || oldTimer.justPlay != justPlay || oldTimer.beginTimestamp != beginTimestamp / 1000 || oldTimer.endTimestamp != endTimestamp / 1000 || oldTimer.afterEvent != afterevent || oldTimer.repeated != repeated || oldTimer.alwaysZap != alwaysZap)
        }

    }

    DisposableEffect(Unit) {
        onDispose { reset() }
    }


    AdaptiveDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        title = if (oldTimer == null) {
            stringResource(R.string.add_timer)
        } else {
            stringResource(R.string.edit_timer)
        },
        actionButton = {
            TextButton(
                enabled = isEverythingValid(),
                onClick = {
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
        },
        content = { isContentScrollable ->
            Column {
                ExposedDropdownMenuBox(
                    expanded = showServicesMenu,
                    onExpandedChange = {
                        showServicesMenu = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = if (serviceReference == "") {
                            ""
                        } else {
                            services
                                .flatMap { it.services.flatMap { service -> service.subservices } }
                                .find { it.serviceReference == serviceReference }?.serviceName
                                ?: ""
                        },
                        onValueChange = { },
                        singleLine = true,
                        readOnly = true,
                        label = { Text(stringResource(R.string.service)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showServicesMenu) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = showServicesMenu,
                        scrollState = rememberScrollState(),
                        onDismissRequest = { showServicesMenu = false },
                        containerColor = if (isContentScrollable) {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        } else {
                            MenuDefaults.containerColor
                        }
                    ) {
                        if (services.isNotEmpty()) {
                            services.forEach { subservice ->
                                subservice.services.first().subservices.forEachIndexed { index, service ->
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Text(
                                                text = "${index + 1}.",
                                                textAlign = TextAlign.Center,
                                            )
                                        },
                                        text = { Text(text = service.serviceName) },
                                        onClick = {
                                            serviceReference = service.serviceReference
                                            showServicesMenu = false
                                        }
                                    )
                                }
                                if (subservice != services.last()) {
                                    HorizontalDivider()
                                }
                            }
                        } else {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp, horizontal = 8.dp)
                            )
                        }
                    }

                }
                Spacer(Modifier.size(8.dp))
                OutlinedTextField(
                    state = titleState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    label = { Text(text = stringResource(R.string.title)) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(Modifier.size(8.dp))
                OutlinedTextField(
                    state = shortDescriptionState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    label = { Text(text = stringResource(R.string.description)) },
                    modifier = Modifier
                        .fillMaxWidth(),
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
                    Modifier
                        .horizontalScroll(toggleScrollState)
                ) {
                    FilterChip(
                        selected = disabled == 0, onClick =
                            {
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
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    Modifier.size(
                                        FilterChipDefaults.IconSize
                                    )
                                )
                            }
                        })
                    FilterChip(
                        selected = justPlay == 1,
                        onClick = {
                            justPlay = if (justPlay == 0) {
                                1
                            } else {
                                0
                            }
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.justplay)
                            )
                        },
                        leadingIcon = {
                            AnimatedVisibility(justPlay == 1) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    Modifier.size(
                                        FilterChipDefaults.IconSize
                                    )
                                )
                            }
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    FilterChip(
                        selected = alwaysZap == 1,
                        onClick = {
                            alwaysZap = if (alwaysZap == 0) {
                                1
                            } else {
                                0
                            }
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.switch_channel)
                            )
                        },
                        leadingIcon = {
                            AnimatedVisibility(alwaysZap == 1) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    Modifier.size(
                                        FilterChipDefaults.IconSize
                                    )
                                )
                            }
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                ExposedDropdownMenuBox(
                    expanded = showAftereventMenu,
                    onExpandedChange = {
                        showAftereventMenu = it
                    },
                    modifier = Modifier
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
                        label = { Text(stringResource(R.string.afterevent)) },
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
                            onClick = { afterevent = 2 })
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.shutdown)) },
                            onClick = { afterevent = 3 })
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.standby)) },
                            onClick = { afterevent = 1 })
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

                if (showBeginDatePicker) {
                    val dateScrollState = rememberScrollState()
                    DatePickerDialog(onDismissRequest = {
                        showBeginDatePicker = false
                    }, confirmButton = {
                        TextButton(onClick = {
                            beginTimestamp = TimestampUtils.combineDateTime(
                                beginTimestamp,
                                beginDateState.selectedDateMillis
                                    ?: beginTimestamp
                            )
                            showBeginDatePicker = false
                        }) { Text(stringResource(R.string.ok)) }

                    }, dismissButton = {
                        TextButton(
                            onClick = {
                                showBeginDatePicker = false
                            },
                        ) { Text(stringResource(R.string.cancel)) }
                    }) {
                        DatePicker(
                            modifier = Modifier.verticalScroll(dateScrollState),
                            state = beginDateState,
                        )
                    }
                }

                if (showEndDatePicker) {
                    val dateScrollState = rememberScrollState()
                    DatePickerDialog(onDismissRequest = {
                        showEndDatePicker = false
                    }, confirmButton = {
                        TextButton(onClick = {
                            endTimestamp = TimestampUtils.combineDateTime(
                                endTimestamp,
                                endDateState.selectedDateMillis
                                    ?: endTimestamp
                            )
                            showEndDatePicker = false
                        }) { Text(stringResource(R.string.ok)) }

                    }, dismissButton = {
                        TextButton(
                            onClick = {
                                showEndDatePicker = false
                            },
                        ) { Text(stringResource(R.string.cancel)) }
                    }) {
                        DatePicker(
                            modifier = Modifier.verticalScroll(dateScrollState),
                            state = endDateState,
                        )
                    }
                }

                if (showBeginTimePicker) {
                    var displayMode by remember { mutableStateOf(TimePickerDisplayMode.Picker) }
                    TimePickerDialog(
                        onDismissRequest = { showBeginTimePicker = false },
                        title = { TimePickerDialogDefaults.Title(displayMode = displayMode) },
                        confirmButton = {
                            TextButton(onClick = {
                                beginTimestamp = TimestampUtils.combineTimeDate(
                                    beginTimestamp,
                                    TimestampUtils.getMillisFromTimeString(
                                        beginTimeState.hour.toString() + ":" + beginTimeState.minute.toString()
                                    )
                                )
                                showBeginTimePicker = false
                            }) {
                                Text(stringResource(R.string.ok))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showBeginTimePicker = false }) {
                                Text(stringResource(R.string.cancel))
                            }
                        },
                        modeToggleButton = {
                            if (LocalWindowInfo.current.containerSize.height.dp > MinHeightForTimePicker) {
                                TimePickerDialogDefaults.DisplayModeToggle(
                                    onDisplayModeChange = {
                                        displayMode =
                                            if (displayMode == TimePickerDisplayMode.Picker) {
                                                TimePickerDisplayMode.Input
                                            } else {
                                                TimePickerDisplayMode.Picker
                                            }
                                    },
                                    displayMode = displayMode,
                                )
                            }
                        }
                    ) {
                        if (
                            displayMode == TimePickerDisplayMode.Picker &&
                            LocalWindowInfo.current.containerSize.height.dp > MinHeightForTimePicker
                        ) {
                            TimePicker(state = beginTimeState)
                        } else {
                            TimeInput(state = beginTimeState)
                        }
                    }
                }

                if (showEndTimePicker) {
                    var displayMode by remember { mutableStateOf(TimePickerDisplayMode.Picker) }
                    TimePickerDialog(
                        onDismissRequest = { showEndTimePicker = false },
                        title = { TimePickerDialogDefaults.Title(displayMode = displayMode) },
                        confirmButton = {
                            TextButton(onClick = {
                                endTimestamp = TimestampUtils.combineTimeDate(
                                    endTimestamp,
                                    TimestampUtils.getMillisFromTimeString(
                                        endTimeState.hour.toString() + ":" + endTimeState.minute.toString()
                                    )
                                )
                                showEndTimePicker = false
                            }) {
                                Text(stringResource(R.string.ok))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showEndTimePicker = false }) {
                                Text(stringResource(R.string.cancel))
                            }
                        },
                        modeToggleButton = {
                            if (LocalWindowInfo.current.containerSize.height.dp > MinHeightForTimePicker) {
                                TimePickerDialogDefaults.DisplayModeToggle(
                                    onDisplayModeChange = {
                                        displayMode =
                                            if (displayMode == TimePickerDisplayMode.Picker) {
                                                TimePickerDisplayMode.Input
                                            } else {
                                                TimePickerDisplayMode.Picker
                                            }
                                    },
                                    displayMode = displayMode,
                                )
                            }
                        }
                    ) {
                        if (
                            displayMode == TimePickerDisplayMode.Picker &&
                            LocalWindowInfo.current.containerSize.height.dp > MinHeightForTimePicker
                        ) {
                            TimePicker(state = endTimeState)
                        } else {
                            TimeInput(state = endTimeState)
                        }
                    }
                }
            }
        }
    )
}