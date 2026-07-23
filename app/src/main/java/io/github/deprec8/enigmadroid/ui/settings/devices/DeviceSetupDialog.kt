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

package io.github.deprec8.enigmadroid.ui.settings.devices

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.common.constant.DefaultPorts
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import io.github.deprec8.enigmadroid.ui.components.DeviceSetupCard
import io.github.deprec8.enigmadroid.ui.components.dialogs.AdaptiveDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceSetupDialog(
    oldDevice: Device? = null, onDismissRequest: () -> Unit, onSave: (Device, Device?) -> Unit
) {
    var https by rememberSaveable { mutableStateOf(oldDevice?.https == true) }
    var login by rememberSaveable { mutableStateOf(oldDevice?.login == true) }

    val nameState = rememberTextFieldState(oldDevice?.name ?: "")
    val hostState = rememberTextFieldState(oldDevice?.host ?: "")
    val portState = rememberTextFieldState((oldDevice?.port ?: DefaultPorts.HTTP).toString())
    val livePortState =
        rememberTextFieldState((oldDevice?.livePort ?: DefaultPorts.LIVE).toString())
    val userState = rememberTextFieldState(oldDevice?.user ?: "")
    val passwordState = rememberTextFieldState(oldDevice?.password ?: "")

    val ready by remember {
        derivedStateOf {
            if (nameState.text.isBlank() || hostState.text.isBlank() || portState.text.isBlank() || livePortState.text.isBlank()) return@derivedStateOf false

            val baseNotEqual =
                oldDevice == null || nameState.text != oldDevice.name || hostState.text != oldDevice.host || portState.text != oldDevice.port.toString() || livePortState.text != oldDevice.livePort.toString()

            return@derivedStateOf if (login) {
                baseNotEqual && userState.text.isNotBlank() && passwordState.text.isNotBlank() && if (oldDevice != null) userState.text != oldDevice.user || passwordState.text != oldDevice.password else true
            } else {
                baseNotEqual
            }
        }
    }

    AdaptiveDialog(
        onDismissRequest = {
            onDismissRequest()
        }, title = if (oldDevice == null) {
            stringResource(id = R.string.add_device)
        } else {
            stringResource(id = R.string.edit_device)
        }, actionButton = {
            TextButton(
                onClick = {
                    if (ready) {
                        onSave(
                            Device(
                                name = nameState.text.toString(),
                                host = hostState.text.toString(),
                                port = portState.text.toString().toInt(),
                                livePort = livePortState.text.toString().toInt(),
                                https = https,
                                login = login,
                                user = userState.text.toString(),
                                password = passwordState.text.toString(),
                            ), oldDevice
                        )
                    }
                }, enabled = ready
            ) {
                Text(
                    text = if (oldDevice == null) {
                        stringResource(id = R.string.create)
                    } else {
                        stringResource(id = R.string.save)
                    }
                )
            }
        }) {
        DeviceSetupCard(
            modifier = Modifier,
            nameState = nameState,
            hostState = hostState,
            portState = portState,
            livePortState = livePortState,
            https = https,
            login = login,
            userState = userState,
            passwordState = passwordState,
            onHttpsChange = {
                https = !https
                if (portState.text == DefaultPorts.HTTP && https) {
                    portState.setTextAndPlaceCursorAtEnd(DefaultPorts.HTTPS)
                } else if (portState.text == DefaultPorts.HTTPS && !https) {
                    portState.setTextAndPlaceCursorAtEnd(DefaultPorts.HTTP)

                }
            },
            onLoginChange = {
                login = !login
            })
    }
}