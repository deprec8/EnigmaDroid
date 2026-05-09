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

package io.github.deprec8.enigmadroid.ui.deviceinfo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.ConnectedTv
import androidx.compose.material.icons.outlined.Router
import androidx.compose.material.icons.outlined.SimCard
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.model.api.DeviceInfo
import io.github.deprec8.enigmadroid.ui.components.NoResults

@Composable
fun DeviceInfoContent(
    modifier: Modifier = Modifier, deviceInfo: DeviceInfo, paddingValues: PaddingValues
) {
    if (deviceInfo != DeviceInfo()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(310.dp),
            modifier = modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues),
            contentPadding = paddingValues
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    ListItem(headlineContent = {
                        Text(stringResource(R.string.hardware))
                    }, leadingContent = {
                        Icon(
                            Icons.Outlined.ConnectedTv, null
                        )
                    })
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                }
            }
            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.brand_model)) },
                    supportingContent = { Text(text = "${deviceInfo.brand} ${deviceInfo.model} (${deviceInfo.boxType})") })
            }
            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.chipset)) },
                    supportingContent = { Text(text = deviceInfo.chipset) })
            }
            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.memory)) },
                    supportingContent = {
                        Text(
                            text = "${deviceInfo.totalMemory} (" + stringResource(
                                R.string.free, deviceInfo.freeMemory
                            ) + ")"
                        )
                    })
            }
            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.uptime)) },
                    supportingContent = { Text(text = deviceInfo.uptime) })
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    ListItem(headlineContent = {
                        Text(stringResource(R.string.software))
                    }, leadingContent = {
                        Icon(
                            Icons.Outlined.Apps, null
                        )
                    })
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                }
            }
            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.oe_system)) },
                    supportingContent = { Text(text = deviceInfo.oeSystemVersion) })
            }
            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.firmware)) },
                    supportingContent = { Text(text = "${deviceInfo.imageDistro} ${deviceInfo.imageVersion} (${deviceInfo.enigmaVersion})") })
            }
            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.kernel_drivers)) },
                    supportingContent = { Text(text = "${deviceInfo.kernelVersion} (${deviceInfo.driverDate})") })
            }
            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.openwebif)) },
                    supportingContent = { Text(text = deviceInfo.owifVersion) })
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    ListItem(headlineContent = {
                        Text(stringResource(R.string.tuner))
                    }, leadingContent = {
                        Icon(
                            Icons.Outlined.SimCard, null
                        )
                    })
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                }
            }
            items(deviceInfo.tuners) { tuner ->
                ListItem(headlineContent = { Text(text = tuner.name) }, supportingContent = {
                    Text(
                        text = tuner.type
                    )
                })
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    ListItem(headlineContent = {
                        Text(stringResource(R.string.interfaces))
                    }, leadingContent = {
                        Icon(
                            Icons.Outlined.Router, null
                        )
                    })
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                }
            }
            items(deviceInfo.interfaces) { iface ->
                ListItem(headlineContent = { Text(text = iface.name) }, supportingContent = {
                    Text(
                        text = buildString {
                            appendLine(stringResource(R.string.ip_address, iface.ip))
                            appendLine(stringResource(R.string.ip_method, iface.ipv4Method))
                            appendLine(stringResource(R.string.ipv6_address, iface.firstPublicIpv6))
                            appendLine(stringResource(R.string.link_speed, iface.linkSpeed))
                            appendLine(stringResource(R.string.gateway, iface.gateway))
                            append(stringResource(R.string.adapter, iface.friendlyNic))
                        })
                })
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    ListItem(headlineContent = {
                        Text(stringResource(R.string.storage))
                    }, leadingContent = {
                        Icon(
                            Icons.Outlined.Storage, null
                        )
                    })
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                }
            }
            items(deviceInfo.hdds) { hdd ->
                ListItem(
                    headlineContent = { Text(text = "${hdd.model} (${hdd.mountDirectory})") },
                    supportingContent = {
                        Text(
                            text = "${hdd.capacity} (" + stringResource(
                                R.string.free, hdd.freeSpace
                            ) + ")"
                        )
                    })
            }
        }
    } else {
        NoResults(
            Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        )
    }
}