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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.objects.DefaultPort
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import io.github.deprec8.enigmadroid.ui.components.AdaptiveDialog
import io.github.deprec8.enigmadroid.ui.components.DeviceSetupCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceSetupDialog(
    oldDevice: Device? = null,
    onDismissRequest: () -> Unit,
    onSave: (newDevice: Device, oldDevice: Device?) -> Unit
) {
    var isHttps by rememberSaveable { mutableStateOf(false) }
    var isLogin by rememberSaveable { mutableStateOf(false) }

    val nameState = rememberTextFieldState("")
    val ipState = rememberTextFieldState("")
    val portState = rememberTextFieldState(DefaultPort.HTTP)
    val livePortState = rememberTextFieldState(DefaultPort.LIVE)
    val userState = rememberTextFieldState("")
    val passwordState = rememberTextFieldState("")

    fun setDeviceData() {
        nameState.setTextAndPlaceCursorAtEnd(oldDevice?.name ?: "")
        ipState.setTextAndPlaceCursorAtEnd(oldDevice?.ip ?: "")
        portState.setTextAndPlaceCursorAtEnd(oldDevice?.port ?: DefaultPort.HTTP)
        livePortState.setTextAndPlaceCursorAtEnd(oldDevice?.livePort ?: DefaultPort.LIVE)
        userState.setTextAndPlaceCursorAtEnd(oldDevice?.user ?: "")
        passwordState.setTextAndPlaceCursorAtEnd(oldDevice?.password ?: "")

        isHttps = oldDevice?.isHttps == true
        isLogin = oldDevice?.isLogin == true
    }

    LaunchedEffect(Unit) {
        setDeviceData()
    }

    fun isSaveReady(): Boolean {
        return if (Device(
                oldDevice?.id ?: 0,
                nameState.text.toString(),
                ipState.text.toString(),
                isHttps,
                isLogin,
                userState.text.toString(),
                passwordState.text.toString(),
                portState.text.toString(),
                livePortState.text.toString()
            ) != oldDevice && nameState.text.isNotBlank() && ipState.text.isNotBlank() && portState.text.isNotBlank() && livePortState.text.isNotBlank()
        ) {
            if (isLogin) {
                userState.text.isNotBlank() && passwordState.text.isNotBlank()
            } else {
                true
            }
        } else {
            false
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
                    if (isSaveReady()) {
                        onSave(
                            Device(
                                0,
                                nameState.text.toString(),
                                ipState.text.toString(),
                                isHttps,
                                isLogin,
                                userState.text.toString(),
                                passwordState.text.toString(),
                                portState.text.toString(),
                                livePortState.text.toString()
                            ), oldDevice
                        )
                    }
                }, enabled = isSaveReady()
            ) {
                Text(
                    text = if (oldDevice == null) {
                        stringResource(id = R.string.create)
                    } else {
                        stringResource(id = R.string.save)
                    }
                )
            }
        }, content = {
            DeviceSetupCard(
                modifier = Modifier,
                nameState = nameState,
                ipState = ipState,
                portState = portState,
                livePortState = livePortState,
                isHttps = isHttps,
                isLogin = isLogin,
                userState = userState,
                passwordState = passwordState,
                onHttpsChange = {
                    isHttps = ! isHttps
                    if (portState.text == DefaultPort.HTTP && isHttps) {
                        portState.setTextAndPlaceCursorAtEnd(DefaultPort.HTTPS)
                    } else if (portState.text == DefaultPort.HTTPS && ! isHttps) {
                        portState.setTextAndPlaceCursorAtEnd(DefaultPort.HTTP)

                    }
                },
                onLoginChange = {
                    isLogin = ! isLogin
                })
        })
}