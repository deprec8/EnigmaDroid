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

package io.github.deprec8.enigmadroid.ui.timers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import io.github.deprec8.enigmadroid.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    state: TimePickerState,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit
) {
    var showDial by rememberSaveable { mutableStateOf(true) }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val pickerScrollState = rememberScrollState()
    val inputScrollState = rememberScrollState()

    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)

        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = stringResource(R.string.select_time),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium
                )
                if (showDial) {
                    TimePicker(
                        modifier = Modifier.verticalScroll(pickerScrollState),
                        layoutType = if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED &&
                            windowSizeClass.windowHeightSizeClass != WindowHeightSizeClass.COMPACT
                        ) {
                            TimePickerLayoutType.Horizontal
                        } else {
                            TimePickerLayoutType.Vertical
                        },
                        state = state
                    )
                } else {
                    TimeInput(
                        modifier = Modifier.verticalScroll(inputScrollState),
                        state = state
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    IconButton(onClick = { showDial = ! showDial }) {
                        Icon(
                            imageVector = if (showDial) {
                                Icons.Outlined.Keyboard
                            } else {
                                Icons.Outlined.AccessTime
                            },
                            contentDescription = stringResource(R.string.toggle_time_picker_type),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = { onDismissRequest() }) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(onClick = {
                        onConfirmRequest()
                    }) {
                        Text(stringResource(R.string.ok))
                    }
                }
            }
        }
    }
}