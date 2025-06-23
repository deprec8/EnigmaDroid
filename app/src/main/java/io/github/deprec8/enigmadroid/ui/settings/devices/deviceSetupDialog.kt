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

package io.github.deprec8.enigmadroid.ui.settings.devices

import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import io.github.deprec8.enigmadroid.ui.components.AdaptiveDialog
import io.github.deprec8.enigmadroid.ui.components.DeviceSetupCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceSetupDialog(
    oldDevice: Device? = null,
    onDismiss: () -> Unit,
    onSave: (newDevice: Device, oldDevice: Device?) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var ip by rememberSaveable { mutableStateOf("") }
    var port by rememberSaveable { mutableStateOf("80") }
    var livePort by rememberSaveable { mutableStateOf("8001") }
    var isHttps by rememberSaveable { mutableStateOf(false) }
    var isLogin by rememberSaveable { mutableStateOf(false) }
    var user by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    rememberScrollState()

    TopAppBarDefaults.pinnedScrollBehavior()

    fun setDeviceData() {
        name = oldDevice?.name ?: ""
        ip = oldDevice?.ip ?: ""
        port = oldDevice?.port ?: "80"
        livePort = oldDevice?.livePort ?: "8001"
        isHttps = oldDevice?.isHttps == true
        isLogin = oldDevice?.isLogin == true
        user = oldDevice?.user ?: ""
        password = oldDevice?.password ?: ""
    }

    LaunchedEffect(Unit) {
        setDeviceData()
    }

    fun isSaveReady(): Boolean {
        return if (Device(
                oldDevice?.id ?: 0,
                name,
                ip,
                isHttps,
                isLogin,
                user,
                password,
                port,
                livePort
            ) != oldDevice && name.isNotBlank() && ip.isNotBlank() && port.isNotBlank() && livePort.isNotBlank()
        ) {
            if (isLogin) {
                user.isNotBlank() && password.isNotBlank()
            } else {
                true
            }
        } else {
            false
        }
    }

    AdaptiveDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = if (oldDevice == null) {
            stringResource(id = R.string.add_device)
        } else {
            stringResource(id = R.string.edit_device)
        },
        action = {
            TextButton(
                onClick = {
                    if (isSaveReady()) {
                        onSave(
                            Device(
                                0,
                                name,
                                ip,
                                isHttps,
                                isLogin,
                                user,
                                password,
                                port,
                                livePort,
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
        },
        content = {
            DeviceSetupCard(
                modifier = Modifier,
                name = name,
                ip = ip,
                port = port,
                livePort = livePort,
                isHttps = isHttps,
                isLogin = isLogin,
                user = user,
                password = password,
                passwordVisible = passwordVisible,
                onNameChange = { name = it },
                onIpChange = { ip = it },
                onPortChange = { port = it },
                onLivePortChange = { livePort = it },
                onHttpsChange = {
                    isHttps = ! isHttps
                    if (port == "80" && isHttps) {
                        port = "443"
                    } else if (port == "443" && ! isHttps) {
                        port = "80"

                    }
                },
                onLoginChange = {
                    isLogin = ! isLogin
                },
                onUserChange = { user = it },
                onPasswordChange = { password = it },
                onPasswordVisibilityChange = {
                    passwordVisible =
                        ! passwordVisible
                }
            )
        }
    )
}