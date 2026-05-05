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

package io.github.deprec8.enigmadroid.ui.remotecontrol.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SettingsPower
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.ResetTv
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.ScreenshotMonitor
import androidx.compose.material.icons.outlined.ToggleOn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.common.enums.RemoteControlPowerKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionMenu(
    enabled: Boolean,
    onFetchScreenshot: () -> Unit,
    onPowerKeyClicked: (RemoteControlPowerKey) -> Unit
) {
    var showMenu by rememberSaveable { mutableStateOf(false) }
    var showPowerMenu by rememberSaveable { mutableStateOf(false) }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isSmallScreenLayout =
        !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) || (windowSizeClass.isHeightAtLeastBreakpoint(
            WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND
        ) && !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND))

    if (isSmallScreenLayout) {
        TooltipBox(
            tooltip = {
                PlainTooltip {
                    Text(stringResource(id = R.string.action_menu))
                }
            },
            state = rememberTooltipState(),
            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                TooltipAnchorPosition.Below, 4.dp
            )
        ) {
            IconButton(
                onClick = { showMenu = true }, enabled = enabled
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.action_menu)
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
                        onPowerKeyClicked(
                            RemoteControlPowerKey.ToggleStandby
                        )
                    }, leadingIcon = {
                        Icon(
                            Icons.Outlined.ToggleOn, contentDescription = null
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
                        onPowerKeyClicked(
                            RemoteControlPowerKey.Restart
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
                        onPowerKeyClicked(
                            RemoteControlPowerKey.RestartGui
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
                        onPowerKeyClicked(
                            RemoteControlPowerKey.Shutdown
                        )
                    }, leadingIcon = {
                        Icon(
                            Icons.Outlined.PowerSettingsNew, contentDescription = null
                        )
                    })
                }
            }
        }
    } else {
        Row {
            TooltipBox(
                tooltip = {
                    PlainTooltip {
                        Text(stringResource(id = R.string.screenshot))
                    }
                },
                state = rememberTooltipState(),
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    TooltipAnchorPosition.Below, 4.dp
                )
            ) {
                IconButton(
                    onClick = { onFetchScreenshot() }, enabled = enabled
                ) {
                    Icon(
                        Icons.Outlined.ScreenshotMonitor,
                        contentDescription = stringResource(id = R.string.screenshot)
                    )
                }
            }
            TooltipBox(
                tooltip = {
                    PlainTooltip {
                        Text(stringResource(R.string.power_menu))
                    }
                },
                state = rememberTooltipState(),
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    TooltipAnchorPosition.Below, 4.dp
                )
            ) {
                IconButton(
                    onClick = { showPowerMenu = true }, enabled = enabled
                ) {
                    Icon(
                        Icons.Default.SettingsPower,
                        contentDescription = stringResource(R.string.power_menu)
                    )
                    DropdownMenu(
                        expanded = showPowerMenu, onDismissRequest = { showPowerMenu = false }) {
                        DropdownMenuItem(text = {
                            Text(
                                text = stringResource(R.string.toggle_standby),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }, onClick = {
                            showPowerMenu = false
                            onPowerKeyClicked(
                                RemoteControlPowerKey.ToggleStandby
                            )
                        }, leadingIcon = {
                            Icon(
                                Icons.Outlined.ToggleOn, contentDescription = null
                            )
                        })
                        DropdownMenuItem(text = {
                            Text(
                                text = stringResource(R.string.restart),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }, onClick = {
                            showPowerMenu = false
                            onPowerKeyClicked(
                                RemoteControlPowerKey.Restart
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
                            showPowerMenu = false
                            onPowerKeyClicked(
                                RemoteControlPowerKey.RestartGui
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
                            showPowerMenu = false
                            onPowerKeyClicked(
                                RemoteControlPowerKey.Shutdown
                            )
                        }, leadingIcon = {
                            Icon(
                                Icons.Outlined.PowerSettingsNew, contentDescription = null
                            )
                        })
                    }
                }
            }
        }
    }
}