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

package io.github.deprec8.enigmadroid.ui.remoteControl.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.ResetTv
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.ScreenshotMonitor
import androidx.compose.material.icons.outlined.SettingsPower
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.enums.RemoteControlPowerButtonType

@Composable
fun ActionMenu(
    enabled: Boolean,
    onFetchScreenshot: () -> Unit,
    onPowerButtonClicked: (RemoteControlPowerButtonType) -> Unit
) {
    var showMenu by rememberSaveable { mutableStateOf(false) }
    IconButton(
        onClick = { showMenu = true }, enabled = enabled
    ) {
        Icon(
            Icons.Default.MoreVert, contentDescription = stringResource(R.string.open_menu)
        )
        DropdownMenu(
            expanded = showMenu, onDismissRequest = { showMenu = false }) {
            DropdownMenuItem(text = {
                Text(
                    text = stringResource(R.string.screenshot),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }, onClick = {
                showMenu = false
                onFetchScreenshot()
            }, leadingIcon = {
                Icon(
                    Icons.Outlined.ScreenshotMonitor, contentDescription = null
                )
            })
            HorizontalDivider()

            DropdownMenuItem(text = {
                Text(
                    text = stringResource(R.string.toggle_standby),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }, onClick = {
                showMenu = false
                onPowerButtonClicked(
                    RemoteControlPowerButtonType.TOGGLE_STANDBY
                )
            }, leadingIcon = {
                Icon(
                    Icons.Outlined.SettingsPower, contentDescription = null
                )
            })
            DropdownMenuItem(text = {
                Text(
                    text = stringResource(R.string.restart),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }, onClick = {
                showMenu = false
                onPowerButtonClicked(
                    RemoteControlPowerButtonType.RESTART
                )
            }, leadingIcon = {
                Icon(
                    Icons.Outlined.RestartAlt, contentDescription = null
                )
            })
            DropdownMenuItem(text = {
                Text(
                    text = stringResource(R.string.restart_gui),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }, onClick = {
                showMenu = false
                onPowerButtonClicked(
                    RemoteControlPowerButtonType.RESTART_GUI
                )
            }, leadingIcon = {
                Icon(
                    Icons.Outlined.ResetTv, contentDescription = null
                )
            })
            DropdownMenuItem(text = {
                Text(
                    text = stringResource(R.string.shutdown),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }, onClick = {
                showMenu = false
                onPowerButtonClicked(
                    RemoteControlPowerButtonType.SHUTDOWN
                )
            }, leadingIcon = {
                Icon(
                    Icons.Outlined.PowerSettingsNew, contentDescription = null
                )
            })
        }
    }
}