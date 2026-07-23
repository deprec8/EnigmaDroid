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

package io.github.deprec8.enigmadroid.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.then
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import io.github.deprec8.enigmadroid.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceSetupCard(
    modifier: Modifier,
    nameState: TextFieldState,
    hostState: TextFieldState,
    portState: TextFieldState,
    livePortState: TextFieldState,
    https: Boolean,
    login: Boolean,
    userState: TextFieldState,
    passwordState: TextFieldState,
    onHttpsChange: () -> Unit,
    onLoginChange: () -> Unit,
) {

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isExpandedScreenLayout =
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        if (isExpandedScreenLayout) {
            Row {
                NameField(
                    nameState, Modifier.fillMaxWidth(0.5f)
                )
                Spacer(Modifier.size(8.dp))
                HostField(
                    hostState, Modifier.fillMaxWidth(1f)
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            PortFields(
                portState = portState, livePortState = livePortState, last = !login
            )
            Settings(
                https = https,
                login = login,
                onHttpsChange = { onHttpsChange() },
                onLoginChange = {
                    passwordVisible = false
                    onLoginChange()
                })
            Row {
                UserField(
                    userState, Modifier.fillMaxWidth(0.5f), login
                )
                Spacer(Modifier.size(16.dp))
                PasswordField(
                    passwordState, Modifier.fillMaxWidth(1f), login, passwordVisible
                ) { passwordVisible = it }
            }
        } else {
            NameField(
                nameState, Modifier.fillMaxWidth()
            )
            Spacer(Modifier.size(8.dp))
            HostField(
                hostState, Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.size(8.dp))
            PortFields(
                portState = portState, livePortState = livePortState, last = !login
            )
            Settings(
                https = https,
                login = login,
                onHttpsChange = { onHttpsChange() },
                onLoginChange = {
                    passwordVisible = false
                    onLoginChange()
                })
            UserField(
                userState, Modifier.fillMaxWidth(), login
            )
            Spacer(Modifier.size(16.dp))
            PasswordField(
                passwordState, Modifier.fillMaxWidth(), login, passwordVisible
            ) { passwordVisible = it }
        }
    }
}

@Composable
private fun NameField(state: TextFieldState, modifier: Modifier) {
    OutlinedTextField(
        state = state,
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            capitalization = KeyboardCapitalization.None,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        label = {
            Text(text = stringResource(R.string.name))
        },
        modifier = modifier
    )
}

@Composable
private fun HostField(state: TextFieldState, modifier: Modifier) {
    OutlinedTextField(
        state = state,
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            capitalization = KeyboardCapitalization.None,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        label = {
            Text(text = stringResource(R.string.ip_address_or_hostname))
        },
        modifier = modifier
    )
}

@Composable
private fun UserField(state: TextFieldState, modifier: Modifier, enabled: Boolean) {
    OutlinedTextField(
        enabled = enabled,
        state = state,
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            capitalization = KeyboardCapitalization.None,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        label = {
            Text(text = stringResource(R.string.username))
        },
        modifier = modifier.semantics {
            contentType = ContentType.Password
        })
}

@Composable
private fun PasswordField(
    state: TextFieldState,
    modifier: Modifier,
    enabled: Boolean,
    visible: Boolean,
    onVisibilityChange: (value: Boolean) -> Unit,
) {
    OutlinedSecureTextField(
        state = state, enabled = enabled, trailingIcon = {
            PasswordVisibilityToggleButton(
                visible, onVisibilityChange, enabled
            )
        }, keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            capitalization = KeyboardCapitalization.None,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ), textObfuscationMode = if (!visible) {
            TextObfuscationMode.Hidden
        } else {
            TextObfuscationMode.Visible
        }, label = {
            Text(text = stringResource(R.string.password))
        }, modifier = modifier.semantics {
            contentType = ContentType.Password
        })
}

@Composable
private fun PortFields(
    portState: TextFieldState, livePortState: TextFieldState, last: Boolean
) {
    Row {
        OutlinedTextField(
            state = portState,
            lineLimits = TextFieldLineLimits.SingleLine,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
            label = {
                Text(text = stringResource(R.string.port))
            },
            inputTransformation = InputTransformation.maxLength(5).then {
                if (!asCharSequence().all { it.isDigit() }) {
                    revertAllChanges()
                    return@then
                }

                if (asCharSequence().toString().toIntOrNull()?.let { it > 65535 } == true) {
                    revertAllChanges()
                }
            },
            modifier = Modifier.fillMaxWidth(0.5f)
        )
        Spacer(Modifier.size(16.dp))
        OutlinedTextField(
            state = livePortState,
            lineLimits = TextFieldLineLimits.SingleLine,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = if (last) {
                    ImeAction.Done
                } else {
                    ImeAction.Next
                }
            ),
            label = {
                Text(text = stringResource(R.string.live_port))
            },
            inputTransformation = InputTransformation.maxLength(5).then {
                if (!asCharSequence().all { it.isDigit() }) {
                    revertAllChanges()
                    return@then
                }

                if (asCharSequence().toString().toIntOrNull()?.let { it > 65535 } == true) {
                    revertAllChanges()
                }
            },
            modifier = Modifier.fillMaxWidth(1f)
        )
    }
}

@Composable
private fun Settings(
    https: Boolean,
    login: Boolean,
    onHttpsChange: () -> Unit,
    onLoginChange: () -> Unit,
) {
    Column {
        Spacer(modifier = Modifier.size(8.dp))
        Row {
            FilterChip(selected = https, onClick = onHttpsChange, label = {
                Text(
                    text = stringResource(R.string.https),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }, leadingIcon = {
                AnimatedVisibility(https) {
                    Icon(
                        Icons.Default.Check, contentDescription = null, Modifier.size(
                            FilterChipDefaults.IconSize
                        )
                    )
                }
            })
            Spacer(Modifier.size(8.dp))
            FilterChip(selected = login, onClick = {
                onLoginChange()
            }, label = {
                Text(
                    text = stringResource(R.string.login),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }, leadingIcon = {
                AnimatedVisibility(login) {
                    Icon(
                        Icons.Default.Check, contentDescription = null, Modifier.size(
                            FilterChipDefaults.IconSize
                        )
                    )
                }
            })
        }
        Spacer(modifier = Modifier.size(8.dp))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordVisibilityToggleButton(
    visible: Boolean, onVisibilityChange: (value: Boolean) -> Unit, enabled: Boolean
) {
    TooltipBox(
        tooltip = {
            PlainTooltip {
                Text(
                    if (visible) stringResource(id = R.string.hide_password) else stringResource(
                        id = R.string.show_password
                    )
                )
            }
        },
        enableUserInput = enabled,
        state = rememberTooltipState(),
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            TooltipAnchorPosition.Above, 4.dp
        )
    ) {
        IconButton(
            onClick = { onVisibilityChange(!visible) }, enabled = enabled
        ) {
            when (visible) {
                true -> {

                    Icon(
                        Icons.Default.VisibilityOff,
                        contentDescription = stringResource(R.string.hide_password)
                    )
                }

                false -> {

                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = stringResource(R.string.show_password)
                    )
                }
            }
        }

    }
}