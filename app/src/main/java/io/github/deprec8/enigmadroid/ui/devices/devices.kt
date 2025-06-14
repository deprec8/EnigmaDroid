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

package io.github.deprec8.enigmadroid.ui.devices

import android.content.pm.ShortcutManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.ui.components.contentWithDrawerWindowInsets
import io.github.deprec8.enigmadroid.ui.components.horizontalSafeContentPadding
import io.github.deprec8.enigmadroid.utils.IntentUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesPage(
    drawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
    devicesViewModel: DevicesViewModel = hiltViewModel(),
) {

    val currentDeviceId by devicesViewModel.currentDeviceId.collectAsStateWithLifecycle()
    val devices by devicesViewModel.allDevices.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current
    val shortcutManager = context.getSystemService(ShortcutManager::class.java)
    var showAddDialog by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.horizontalSafeContentPadding(true),
                title = {
                    Text(
                        text = stringResource(id = R.string.devices),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    if (windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.EXPANDED ||
                        windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = stringResource(id = R.string.open_menu)
                            )
                        }
                    }
                })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.horizontalSafeContentPadding(true),
                onClick =
                    {
                        showAddDialog = true

                    }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_device),
                )
            }
        },
        contentWindowInsets = contentWithDrawerWindowInsets(),
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        if (devices.isEmpty()) {
            Column(
                Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.no_devices_found),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(innerPadding),
                contentPadding = innerPadding,
                columns = GridCells.Adaptive(300.dp),
            ) {
                itemsIndexed(devices, key = { index, device -> device.id }) { index, device ->
                    var showDropDownMenu by rememberSaveable {
                        mutableStateOf(false)
                    }
                    var showDeleteDialog by rememberSaveable {
                        mutableStateOf(false)
                    }
                    var showEditDialog by rememberSaveable {
                        mutableStateOf(false)
                    }
                    ListItem(
                        headlineContent = { Text(text = device.name) },
                        supportingContent = {
                            Text(
                                text = device.ip + ":" + device.port
                            )
                        },
                        modifier = Modifier
                            .clickable(
                                onClick =
                                    {
                                        if (currentDeviceId != index) {
                                            devicesViewModel.setCurrentDevice(index)
                                        }
                                    })
                            .animateItem(),
                        leadingContent = {
                            AnimatedVisibility(
                                visible = currentDeviceId == index,
                                enter =
                                    expandIn(expandFrom = Alignment.Center) + fadeIn(),
                                exit = shrinkOut(shrinkTowards = Alignment.Center) + fadeOut()
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = stringResource(R.string.current_device)
                                )
                            }
                        },
                        trailingContent = {
                            IconButton(onClick = { showDropDownMenu = true }) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = stringResource(R.string.editing_menu)
                                )
                            }
                            DropdownMenu(
                                expanded = showDropDownMenu,
                                onDismissRequest = { showDropDownMenu = false }) {
                                if (shortcutManager !!.isRequestPinShortcutSupported) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.pin_webif)) },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Outlined.PushPin,
                                                contentDescription = null
                                            )
                                        },
                                        onClick =
                                            {
                                                showDropDownMenu = false
                                                IntentUtils.pinOWIFDevice(
                                                    context,
                                                    device,
                                                    devicesViewModel.makeDeviceOWIFURL(device)
                                                )

                                            })
                                    HorizontalDivider()
                                }
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.edit)) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.Edit,
                                            contentDescription = null
                                        )
                                    },
                                    onClick =
                                        {
                                            showDropDownMenu = false
                                            showEditDialog = true

                                        })

                                DropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.delete)) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.Delete,
                                            contentDescription = null
                                        )
                                    },
                                    onClick =
                                        {
                                            showDropDownMenu = false
                                            showDeleteDialog = true

                                        })
                            }
                        })

                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest =
                                {
                                    showDeleteDialog = false

                                },
                            title = { Text(text = stringResource(R.string.delete_device)) },
                            text = { Text(text = stringResource(R.string.if_you_delete_this_device_it_will_not_be_recoverable)) },
                            icon = {
                                Icon(
                                    Icons.Outlined.Delete,
                                    contentDescription = null
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick =
                                        {
                                            showDeleteDialog = false
                                            devicesViewModel.deleteDevice(index)

                                        }) { Text(stringResource(R.string.confirm)) }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showDeleteDialog = false
                                }) { Text(stringResource(R.string.cancel)) }
                            }
                        )
                    }
                    if (showEditDialog) {
                        DeviceSetupDialog(
                            onDismiss = { showEditDialog = false },
                            oldDevice = device,
                            onSave = { newDevice, oldDevice ->
                                devicesViewModel.editDevice(oldDevice !!, newDevice)
                                showEditDialog = false
                            }
                        )
                    }

                }
            }
        }
        if (showAddDialog) {
            DeviceSetupDialog(
                onDismiss = { showAddDialog = false },
                onSave = { newDevice, _ ->
                    devicesViewModel.addDevice(newDevice)
                    showAddDialog = false
                }
            )
        }
    }
}