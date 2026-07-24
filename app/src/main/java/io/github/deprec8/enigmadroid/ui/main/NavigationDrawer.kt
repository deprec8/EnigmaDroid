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

package io.github.deprec8.enigmadroid.ui.main

import android.content.ActivityNotFoundException
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Dvr
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.automirrored.outlined.Dvr
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.outlined.DeveloperBoard
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Radio
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.common.constant.MainKeys
import io.github.deprec8.enigmadroid.data.ConnectionState
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import io.github.deprec8.enigmadroid.model.DrawerPage
import io.github.deprec8.enigmadroid.model.DrawerPageGroup
import io.github.deprec8.enigmadroid.ui.components.dialogs.UrlIntentErrorDialog
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(
    currentDevice: Device?,
    devices: List<Device>,
    connectionState: ConnectionState,
    currentTopLevelRoute: NavKey,
    scrollState: ScrollState,
    onCheckConnection: () -> Unit,
    onNavigate: (NavKey) -> Unit,
    onSetCurrentDeviceId: (Long) -> Unit
) {
    Column(
        Modifier
            .verticalScroll(scrollState)
            .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical).asPaddingValues())
            .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start).asPaddingValues())
            .imePadding()
    ) {
        DeviceItem(
            currentDevice = currentDevice,
            devices = devices,
            connectionState = connectionState,
            onCheckConnection = { onCheckConnection() },
            onSetCurrentDeviceId = onSetCurrentDeviceId
        )

        drawerPageGroups.forEachIndexed { index, group ->
            HorizontalDivider(
                Modifier.padding(
                    start = 28.dp,
                    end = 28.dp,
                    top = if (index == 0) 0.dp else 16.dp,
                )
            )
            Text(
                text = stringResource(group.nameRes),
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
                style = MaterialTheme.typography.titleSmall
            )
            group.pages.forEach { drawerPage ->
                DrawerItem(
                    drawerPage,
                    isSelected = currentTopLevelRoute == drawerPage.navKey,
                    onNavigate = {
                        onNavigate(drawerPage.navKey)
                    })
            }
        }
    }
}

@Composable
fun DrawerItem(
    drawerPage: DrawerPage, isSelected: Boolean, onNavigate: (NavKey) -> Unit
) {
    NavigationDrawerItem(
        label = { Text(text = stringResource(drawerPage.nameRes)) }, icon = {
            if (isSelected) {
                Icon(drawerPage.selectedIcon, contentDescription = null)
            } else {
                Icon(drawerPage.icon, contentDescription = null)
            }
        }, selected = isSelected, onClick = {
            onNavigate(drawerPage.navKey)
        }, modifier = Modifier.padding(start = 16.dp, end = 16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceItem(
    currentDevice: Device?,
    devices: List<Device>,
    connectionState: ConnectionState,
    onCheckConnection: () -> Unit,
    onSetCurrentDeviceId: (Long) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showDevicesMenu by rememberSaveable {
        mutableStateOf(false)
    }
    var showUrlIntentErrorDialog by rememberSaveable {
        mutableStateOf(false)
    }

    ListItem(
        headlineContent = {
            Text(
                text = if (devices.isNotEmpty()) {
                    currentDevice?.name ?: stringResource(R.string.no_device_selected)
                } else {
                    stringResource(R.string.no_device_available)
                },
            )
        },
        trailingContent = {
            AnimatedContent(connectionState, label = "", transitionSpec = {
                scaleIn(
                    initialScale = 0f, animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn() togetherWith scaleOut(targetScale = 0f) + fadeOut()
            }) {
                when (it) {
                    ConnectionState.CONNECTED if currentDevice != null -> {
                        TooltipBox(
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(id = R.string.openwebif))
                                }
                            },
                            state = rememberTooltipState(),
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Below, 4.dp
                            )
                        ) {
                            IconButton(onClick = {
                                scope.launch {
                                    try {
                                        CustomTabsIntent.Builder().setShowTitle(true)
                                            .setDownloadButtonEnabled(false)
                                            .setBookmarksButtonEnabled(false).setShareState(
                                                CustomTabsIntent.SHARE_STATE_ON
                                            ).setUrlBarHidingEnabled(true).build().launchUrl(
                                                context, currentDevice.buildOWifUri()
                                            )
                                    } catch (_: ActivityNotFoundException) {
                                        showUrlIntentErrorDialog = true
                                    }
                                }
                            }) {
                                Icon(
                                    Icons.Default.Web,
                                    contentDescription = stringResource(R.string.openwebif),
                                )
                            }
                        }
                    }

                    ConnectionState.CONNECTING -> {
                        IconButton(onClick = {}, enabled = false) {
                            CircularProgressIndicator(Modifier.size(24.dp))
                        }
                    }

                    else -> {
                        TooltipBox(
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(id = R.string.retry))
                                }
                            },
                            state = rememberTooltipState(),
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Below, 4.dp
                            )
                        ) {
                            IconButton(onClick = { onCheckConnection() }) {
                                Icon(
                                    Icons.Default.RestartAlt,
                                    contentDescription = stringResource(R.string.retry),
                                )
                            }
                        }
                    }
                }

            }
        },
        supportingContent = {
            AnimatedContent(
                connectionState,
                label = "",
                transitionSpec = { fadeIn() togetherWith fadeOut() }) {
                when (it) {
                    ConnectionState.CONNECTED -> {
                        currentDevice?.let { device ->
                            Text(
                                "${device.host}:${device.port}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    ConnectionState.NOT_CONNECTED -> {
                        Text(
                            stringResource(id = R.string.unable_to_connect),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    ConnectionState.NO_DEVICE_AVAILABLE -> {
                        Text(
                            stringResource(R.string.first_add_a_device),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    ConnectionState.NO_DEVICE_SELECTED -> {
                        Text(
                            stringResource(R.string.first_select_a_device),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    ConnectionState.CONNECTING -> {
                        Text(
                            stringResource(R.string.connecting),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    ConnectionState.INVALID_DEVICE_RESPONSE -> {
                        Text(
                            stringResource(id = R.string.invalid_response),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        },
        leadingContent = if (devices.size > 1) {
            {
                TooltipBox(
                    tooltip = {
                        PlainTooltip {
                            Text(stringResource(R.string.switch_device))
                        }
                    },
                    state = rememberTooltipState(),
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Below, 4.dp
                    )
                ) {
                    IconButton(onClick = { showDevicesMenu = true }) {
                        Icon(
                            Icons.Default.SwapVert,
                            contentDescription = stringResource(R.string.switch_device),
                        )
                        DropdownMenu(
                            expanded = showDevicesMenu,
                            onDismissRequest = { showDevicesMenu = false }) {
                            devices.forEach { device ->
                                DropdownMenuItem(text = {
                                    Text(device.name)
                                }, onClick = {
                                    if (device.id != currentDevice?.id) onSetCurrentDeviceId(device.id)
                                    showDevicesMenu = false
                                }, leadingIcon = {
                                    if (device.id == currentDevice?.id) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = stringResource(R.string.current_device)
                                        )
                                    }
                                })
                            }
                        }
                    }
                }
            }

        } else {
            null
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.padding(
            horizontal = 12.dp
        ))

    if (showUrlIntentErrorDialog) {
        UrlIntentErrorDialog {
            showUrlIntentErrorDialog = false
        }
    }
}

private val drawerPageGroups = listOf(
    DrawerPageGroup(
        R.string.content, listOf(
            DrawerPage(
                R.string.tv, MainKeys.Tv, Icons.Outlined.LiveTv, Icons.Filled.LiveTv
            ), DrawerPage(
                R.string.radio, MainKeys.Radio, Icons.Outlined.Radio, Icons.Filled.Radio
            ), DrawerPage(
                R.string.current,
                MainKeys.Current,
                Icons.AutoMirrored.Outlined.PlaylistPlay,
                Icons.AutoMirrored.Filled.PlaylistPlay
            ), DrawerPage(
                R.string.movies, MainKeys.Movies, Icons.Outlined.Movie, Icons.Filled.Movie
            ), DrawerPage(
                R.string.timers, MainKeys.Timers, Icons.Outlined.Timer, Icons.Filled.Timer
            ), DrawerPage(
                R.string.tv_epg,
                MainKeys.TvEpg,
                Icons.AutoMirrored.Outlined.Dvr,
                Icons.AutoMirrored.Filled.Dvr,
            ), DrawerPage(
                R.string.radio_epg,
                MainKeys.RadioEpg,
                Icons.AutoMirrored.Outlined.LibraryBooks,
                Icons.AutoMirrored.Filled.LibraryBooks,
            )
        )
    ), DrawerPageGroup(
        R.string.device, listOf(
            DrawerPage(
                R.string.device_info,
                MainKeys.DeviceInfo,
                Icons.Outlined.DeveloperBoard,
                Icons.Filled.DeveloperBoard
            ), DrawerPage(
                R.string.signal, MainKeys.Signal, Icons.Outlined.Speed, Icons.Filled.Speed
            )
        )
    ), DrawerPageGroup(
        R.string.settings, listOf(
            DrawerPage(
                R.string.settings, MainKeys.Settings, Icons.Outlined.Settings, Icons.Filled.Settings
            ),
        )
    )
)