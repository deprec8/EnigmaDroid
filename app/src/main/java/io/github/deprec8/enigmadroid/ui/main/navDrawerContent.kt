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

package io.github.deprec8.enigmadroid.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Dvr
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.outlined.Dvr
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Radio
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.github.deprec8.enigmadroid.R
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import io.github.deprec8.enigmadroid.model.drawer.DrawerGroup
import io.github.deprec8.enigmadroid.model.drawer.DrawerPage
import io.github.deprec8.enigmadroid.model.navigation.MainPages
import io.github.deprec8.enigmadroid.utils.IntentUtils
import kotlinx.coroutines.launch
import kotlin.reflect.KSuspendFunction0

@Composable
fun NavDrawerContent(
    currentDevice: Device?,
    deviceStatus: Int?,
    makeOWIFURL: KSuspendFunction0<String>,
    updateDeviceStatus: () -> Unit,
    navController: NavHostController,
    modalDrawerState: DrawerState
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val scrollState = rememberScrollState()

    val drawerGroups = listOf(
        DrawerGroup(
            stringResource(R.string.content),
            listOf(
                DrawerPage(
                    stringResource(R.string.tv),
                    MainPages.TV,
                    Icons.Outlined.LiveTv,
                    Icons.Filled.LiveTv
                ),
                DrawerPage(
                    stringResource(R.string.radio),
                    MainPages.Radio,
                    Icons.Outlined.Radio,
                    Icons.Filled.Radio
                ),
                DrawerPage(
                    stringResource(R.string.current),
                    MainPages.Current,
                    Icons.Outlined.PlayArrow,
                    Icons.Filled.PlayArrow
                ),
                DrawerPage(
                    stringResource(R.string.movies),
                    MainPages.Movies,
                    Icons.Outlined.Movie,
                    Icons.Filled.Movie
                ),
                DrawerPage(
                    stringResource(R.string.timers),
                    MainPages.Timers,
                    Icons.Outlined.Timer,
                    Icons.Filled.Timer
                ),
                DrawerPage(
                    stringResource(R.string.tv_epg),
                    MainPages.TVEPG,
                    Icons.AutoMirrored.Outlined.Dvr,
                    Icons.AutoMirrored.Filled.Dvr,
                ),
                DrawerPage(
                    stringResource(R.string.radio_epg),
                    MainPages.RadioEPG,
                    Icons.AutoMirrored.Outlined.LibraryBooks,
                    Icons.AutoMirrored.Filled.LibraryBooks,
                )
            )
        ),
        DrawerGroup(
            stringResource(R.string.device),
            listOf(
                DrawerPage(
                    stringResource(R.string.deviceinfo),
                    MainPages.DeviceInfo,
                    Icons.Outlined.Tv,
                    Icons.Filled.Tv
                ),
                DrawerPage(
                    stringResource(R.string.signal),
                    MainPages.Signal,
                    Icons.Outlined.Speed,
                    Icons.Filled.Speed
                )
            )
        ),
        DrawerGroup(
            stringResource(R.string.settings),
            listOf(
                DrawerPage(
                    stringResource(R.string.settings),
                    MainPages.Settings,
                    Icons.Outlined.Settings,
                    Icons.Filled.Settings
                ),
            )
        )
    )

    fun closeNavDrawer() {
        scope.launch {
            modalDrawerState.apply {
                close()
            }
        }
    }

    Column(
        Modifier
            .verticalScroll(scrollState)
            .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical).asPaddingValues())
            .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start).asPaddingValues())
            .imePadding()
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = currentDevice?.name ?: stringResource(R.string.no_device_available),
                )
            },
            trailingContent = {
                AnimatedContent(deviceStatus, label = "", transitionSpec = {
                    scaleIn(
                        initialScale = 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() togetherWith
                            scaleOut(targetScale = 0f) + fadeOut()
                })
                {
                    when (it) {
                        0       -> {
                            IconButton(onClick = {
                                scope.launch {
                                    IntentUtils.openOWIF(context, makeOWIFURL())
                                }
                            }) {
                                Icon(
                                    Icons.Default.Web,
                                    contentDescription = stringResource(R.string.open_openwebif),
                                )
                            }
                        }
                        1, 2    -> {
                            IconButton(onClick = updateDeviceStatus) {
                                Icon(
                                    Icons.Default.RestartAlt,
                                    contentDescription = stringResource(R.string.retry),
                                )
                            }
                        }
                        null, 3 -> {
                            IconButton(onClick = {}, enabled = false) {
                                CircularProgressIndicator(Modifier.size(24.dp))
                            }
                        }
                    }

                }
            },
            supportingContent = {
                AnimatedContent(
                    deviceStatus,
                    label = "",
                    transitionSpec = { fadeIn() togetherWith fadeOut() }) {
                    when (it) {
                        0       -> {
                            Text(
                                stringResource(R.string.connected),
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        }
                        1       -> {
                            Text(
                                stringResource(id = R.string.device_not_connected),
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        }
                        2       -> {
                            Text(
                                stringResource(R.string.add_a_device_to_connect_to),
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        }
                        null, 3 -> {
                            Text(
                                stringResource(R.string.searching_for_device),
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            modifier = Modifier.padding(
                horizontal = 12.dp
            )
        )


        drawerGroups.forEachIndexed { index, group ->
            HorizontalDivider(
                Modifier.padding(
                    start = 28.dp,
                    end = 28.dp,
                    top = if (index == 0) 0.dp else 16.dp,
                )
            )
            Text(
                text = group.name,
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
                style = MaterialTheme.typography.titleSmall
            )
            group.pages.forEach { drawerPage ->
                NavigationDrawerItem(
                    label = { Text(text = drawerPage.name) },
                    icon = {
                        when (currentDestination?.hierarchy?.any {
                            it.hasRoute(
                                drawerPage.route::class
                            )
                        }) {
                            true -> Icon(drawerPage.selectedIcon, contentDescription = null)
                            else -> Icon(drawerPage.icon, contentDescription = null)
                        }
                    },
                    selected = currentDestination?.hierarchy?.any { it.hasRoute(drawerPage.route::class) } == true,
                    onClick = {
                        if (currentDestination?.hierarchy?.any { it.hasRoute(drawerPage.route::class) } == false) {
                            navController.navigate(drawerPage.route)
                        }
                        closeNavDrawer()
                    },
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                )
            }
        }
    }
}