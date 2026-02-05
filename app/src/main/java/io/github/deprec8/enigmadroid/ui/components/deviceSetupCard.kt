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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import io.github.deprec8.enigmadroid.R


@Composable
fun DeviceSetupCard(
    modifier: Modifier,
    nameState: TextFieldState,
    ipState: TextFieldState,
    portState: TextFieldState,
    livePortState: TextFieldState,
    isHttps: Boolean,
    isLogin: Boolean,
    userState: TextFieldState,
    passwordState: TextFieldState,
    onHttpsChange: () -> Unit,
    onLoginChange: () -> Unit,
) {

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)) {
            Row {
                OutlinedTextField(
                    state = nameState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                    ),
                    label = {
                        Text(
                            text = stringResource(R.string.name),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .fillMaxWidth(0.5f)
                )
                OutlinedTextField(
                    state = portState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                    ),
                    label = {
                        Text(
                            text = stringResource(R.string.port),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .fillMaxWidth(1f)
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Row {
                OutlinedTextField(
                    state = ipState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                    ),
                    label = {
                        Text(
                            text = stringResource(R.string.ip_address),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .fillMaxWidth(0.5f)
                )
                OutlinedTextField(
                    state = livePortState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number, imeAction = if (! isLogin) {
                            ImeAction.Done
                        } else {
                            ImeAction.Next
                        }
                    ),
                    label = {
                        Text(
                            text = stringResource(R.string.live_port),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .fillMaxWidth(1f)
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Row {
                FilterChip(selected = isHttps, onClick = onHttpsChange, label = {
                    Text(
                        text = stringResource(R.string.https),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }, leadingIcon = {
                    AnimatedVisibility(isHttps) {
                        Icon(
                            Icons.Default.Check, contentDescription = null, Modifier.size(
                                FilterChipDefaults.IconSize
                            )
                        )
                    }
                })
                FilterChip(
                    selected = isLogin, onClick = {
                        passwordVisible = false
                        onLoginChange()
                    }, label = {
                        Text(
                            text = stringResource(R.string.login),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }, leadingIcon = {
                        AnimatedVisibility(isLogin) {
                            Icon(
                                Icons.Default.Check, contentDescription = null, Modifier.size(
                                    FilterChipDefaults.IconSize
                                )
                            )
                        }
                    }, modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Row {
                OutlinedTextField(
                    enabled = isLogin,
                    state = userState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                    ),
                    label = {
                        Text(
                            text = stringResource(R.string.username),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .fillMaxWidth(0.5f)
                        .semantics {
                            contentType = ContentType.Username
                        })
                OutlinedSecureTextField(
                    state = passwordState, enabled = isLogin, trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible = ! passwordVisible }, enabled = isLogin
                        ) {
                            when (passwordVisible) {
                                true  -> {
                                    Icon(
                                        Icons.Default.VisibilityOff,
                                        contentDescription = stringResource(R.string.toggle_password_visibility)
                                    )
                                }
                                false -> {
                                    Icon(
                                        Icons.Default.Visibility,
                                        contentDescription = stringResource(R.string.toggle_password_visibility)
                                    )
                                }
                            }
                        }
                    }, keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ), textObfuscationMode = if (! passwordVisible) {
                        TextObfuscationMode.Hidden
                    } else {
                        TextObfuscationMode.Visible
                    }, label = {
                        Text(
                            text = stringResource(R.string.password),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }, modifier = Modifier
                        .padding(start = 8.dp)
                        .fillMaxWidth(1f)
                        .semantics {
                            contentType = ContentType.Password
                        })

            }
        } else {
            OutlinedTextField(
                state = nameState,
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                ),
                label = {
                    Text(
                        text = stringResource(R.string.name),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.size(8.dp))
            OutlinedTextField(
                state = ipState,
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                ),
                label = {
                    Text(
                        text = stringResource(R.string.ip_address),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.size(8.dp))
            Row {
                OutlinedTextField(
                    state = portState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                    ),
                    label = {
                        Text(
                            text = stringResource(R.string.port),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .fillMaxWidth(0.5f)
                )
                OutlinedTextField(
                    state = livePortState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number, imeAction = if (! isLogin) {
                            ImeAction.Done
                        } else {
                            ImeAction.Next
                        }
                    ),
                    label = {
                        Text(
                            text = stringResource(R.string.live_port),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .fillMaxWidth(1f)
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Row {
                FilterChip(selected = isHttps, onClick = onHttpsChange, label = {
                    Text(
                        text = stringResource(R.string.https),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }, leadingIcon = {
                    AnimatedVisibility(isHttps) {
                        Icon(
                            Icons.Default.Check, contentDescription = null, Modifier.size(
                                FilterChipDefaults.IconSize
                            )
                        )
                    }
                })
                FilterChip(
                    selected = isLogin, onClick = {
                        passwordVisible = false
                        onLoginChange()
                    }, label = {
                        Text(
                            text = stringResource(R.string.login),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }, leadingIcon = {
                        AnimatedVisibility(isLogin) {
                            Icon(
                                Icons.Default.Check, contentDescription = null, Modifier.size(
                                    FilterChipDefaults.IconSize
                                )
                            )
                        }
                    }, modifier = Modifier.padding(start = 8.dp)
                )
            }


            Spacer(modifier = Modifier.size(8.dp))
            OutlinedTextField(
                enabled = isLogin,
                state = userState,
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                ),
                label = {
                    Text(
                        text = stringResource(R.string.username),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentType = ContentType.Username
                    })
            Spacer(modifier = Modifier.size(8.dp))

            OutlinedSecureTextField(
                state = passwordState, enabled = isLogin, trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = ! passwordVisible }, enabled = isLogin
                    ) {
                        when (passwordVisible) {
                            true  -> {
                                Icon(
                                    Icons.Default.VisibilityOff,
                                    contentDescription = stringResource(R.string.toggle_password_visibility)
                                )
                            }
                            false -> {
                                Icon(
                                    Icons.Default.Visibility,
                                    contentDescription = stringResource(R.string.toggle_password_visibility)
                                )
                            }
                        }
                    }
                }, keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ), textObfuscationMode = if (! passwordVisible) {
                    TextObfuscationMode.Hidden
                } else {
                    TextObfuscationMode.Visible
                }, label = {
                    Text(
                        text = stringResource(R.string.password),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }, modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentType = ContentType.Password
                    })
        }
    }
}