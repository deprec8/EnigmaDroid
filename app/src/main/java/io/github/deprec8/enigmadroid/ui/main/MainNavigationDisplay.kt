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

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import io.github.deprec8.enigmadroid.common.constant.MainKeys
import io.github.deprec8.enigmadroid.common.constant.SettingsKeys
import io.github.deprec8.enigmadroid.common.enums.ContentType
import io.github.deprec8.enigmadroid.ui.components.isSmallScreenLayout
import io.github.deprec8.enigmadroid.ui.components.navigation.DrawerNavigator
import io.github.deprec8.enigmadroid.ui.components.navigation.fadeThroughTransition
import io.github.deprec8.enigmadroid.ui.components.navigation.rememberDrawerNavigationState
import io.github.deprec8.enigmadroid.ui.components.navigation.sharedAxisXTransition
import io.github.deprec8.enigmadroid.ui.components.navigation.toEntries
import io.github.deprec8.enigmadroid.ui.current.CurrentPage
import io.github.deprec8.enigmadroid.ui.deviceinfo.DeviceInfoPage
import io.github.deprec8.enigmadroid.ui.epg.EpgPage
import io.github.deprec8.enigmadroid.ui.live.LivePage
import io.github.deprec8.enigmadroid.ui.movies.MoviesDirectoryPage
import io.github.deprec8.enigmadroid.ui.movies.MoviesPage
import io.github.deprec8.enigmadroid.ui.movies.MoviesViewModel
import io.github.deprec8.enigmadroid.ui.serviceepg.ServiceEpgPage
import io.github.deprec8.enigmadroid.ui.settings.SettingsPage
import io.github.deprec8.enigmadroid.ui.settings.about.AboutPage
import io.github.deprec8.enigmadroid.ui.settings.about.LibrariesPage
import io.github.deprec8.enigmadroid.ui.settings.devices.DevicesPage
import io.github.deprec8.enigmadroid.ui.settings.remotecontrol.RemoteControlSettingsPage
import io.github.deprec8.enigmadroid.ui.settings.search.SearchSettingsPage
import io.github.deprec8.enigmadroid.ui.signal.SignalPage
import io.github.deprec8.enigmadroid.ui.timers.TimersPage
import io.github.deprec8.enigmadroid.utils.IntentUtils
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainNavigationDisplay(
    onNavigateToRemoteControl: () -> Unit, mainViewModel: MainViewModel = koinViewModel()
) {
    val currentDevice by mainViewModel.currentDevice.collectAsStateWithLifecycle()
    val devices by mainViewModel.devices.collectAsStateWithLifecycle()
    val connectionState by mainViewModel.connectionState.collectAsStateWithLifecycle()

    val isSmallScreenLayout = isSmallScreenLayout()
    val modalDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val drawerScrollState = rememberScrollState()

    val drawerNavigationState = rememberDrawerNavigationState(
        MainKeys.Tv, setOf(
            MainKeys.Tv,
            MainKeys.Radio,
            MainKeys.Current,
            MainKeys.Movies,
            MainKeys.Timers,
            MainKeys.TvEpg,
            MainKeys.RadioEpg,
            MainKeys.DeviceInfo,
            MainKeys.Signal,
            MainKeys.Settings
        )
    )

    val drawerNavigator = remember { DrawerNavigator(drawerNavigationState) }

    val entryProvider = entryProvider {
        entry<MainKeys.Tv>(
            metadata = fadeThroughTransition()
        ) {
            LivePage(
                contentType = ContentType.Tv,
                onNavigateToRemoteControl = { onNavigateToRemoteControl() },
                onNavigateToServiceEpg = { serviceReference, serviceName ->
                    drawerNavigator.navigate(
                        MainKeys.ServiceEpg(
                            serviceReference, serviceName
                        )
                    )
                },
                modalDrawerState
            )
        }
        entry<MainKeys.Radio>(
            metadata = fadeThroughTransition()
        ) {
            LivePage(
                contentType = ContentType.Radio,
                onNavigateToRemoteControl = { onNavigateToRemoteControl() },
                onNavigateToServiceEpg = { serviceReference, serviceName ->
                    drawerNavigator.navigate(
                        MainKeys.ServiceEpg(
                            serviceReference, serviceName
                        )
                    )
                },
                modalDrawerState
            )
        }
        entry<MainKeys.Current>(
            metadata = fadeThroughTransition()
        ) {
            CurrentPage(
                onNavigateToRemoteControl = { onNavigateToRemoteControl() },
                onNavigateToServiceEpg = { serviceReference, serviceName ->
                    drawerNavigator.navigate(
                        MainKeys.ServiceEpg(
                            serviceReference, serviceName
                        )
                    )
                },
                drawerState = modalDrawerState
            )
        }
        entry<MainKeys.ServiceEpg>(
            metadata = sharedAxisXTransition()
        ) { backStackEntry ->
            ServiceEpgPage(
                serviceName = backStackEntry.serviceName,
                serviceReference = backStackEntry.serviceReference,
                onNavigateBack = { drawerNavigator.goBack() })
        }
        entry<MainKeys.Movies>(
            metadata = fadeThroughTransition()
        ) {
            MoviesPage(
                onNavigateToRemoteControl = { onNavigateToRemoteControl() },
                onNavigateToDirectory = { connectedDeviceId, path, preloadBatch, freeSpace ->
                    drawerNavigator.navigate(
                        MainKeys.MoviesDirectory(
                            connectedDeviceId, path, preloadBatch, freeSpace
                        )
                    )
                },
                modalDrawerState
            )
        }
        entry<MainKeys.MoviesDirectory>(
            metadata = sharedAxisXTransition()
        ) { backStackEntry ->
            val moviesViewModel: MoviesViewModel = koinViewModel()
            LaunchedEffect(backStackEntry) {
                moviesViewModel.initialize(
                    backStackEntry.connectedDeviceId,
                    backStackEntry.path,
                    backStackEntry.preloadBatch,
                    backStackEntry.freeSpace
                )
            }
            MoviesDirectoryPage(
                onNavigateToRemoteControl = { onNavigateToRemoteControl() },
                onNavigateToDirectory = { connectedDeviceId, path, preloadBatch, freeSpace ->
                    drawerNavigator.navigate(
                        MainKeys.MoviesDirectory(
                            connectedDeviceId, path, preloadBatch, freeSpace
                        )
                    )
                },
                onNavigateBack = {
                    drawerNavigator.goBack()
                },
                onNavigateToTop = {
                    drawerNavigator.goTop()
                },
                moviesViewModel
            )
        }
        entry<MainKeys.Timers>(
            metadata = fadeThroughTransition()
        ) {
            TimersPage(
                onNavigateToRemoteControl = { onNavigateToRemoteControl() }, modalDrawerState
            )
        }
        entry<MainKeys.TvEpg>(
            metadata = fadeThroughTransition()
        ) {
            EpgPage(
                contentType = ContentType.Tv,
                onNavigateToRemoteControl = { onNavigateToRemoteControl() },
                modalDrawerState
            )
        }
        entry<MainKeys.RadioEpg>(
            metadata = fadeThroughTransition()
        ) {
            EpgPage(
                contentType = ContentType.Radio,
                onNavigateToRemoteControl = { onNavigateToRemoteControl() },
                modalDrawerState
            )
        }
        entry<MainKeys.DeviceInfo>(
            metadata = fadeThroughTransition()
        ) {
            DeviceInfoPage(
                onNavigateToRemoteControl = { onNavigateToRemoteControl() }, modalDrawerState
            )
        }
        entry<MainKeys.Signal>(
            metadata = fadeThroughTransition()
        ) {
            SignalPage(
                onNavigateToRemoteControl = { onNavigateToRemoteControl() }, modalDrawerState
            )
        }
        entry<MainKeys.Settings>(
            metadata = fadeThroughTransition()
        ) {
            SettingsPage(
                modalDrawerState, onNavigateToSettingsPage = {
                    drawerNavigator.navigate(it)
                })
        }
        entry<SettingsKeys.About>(
            metadata = sharedAxisXTransition()
        ) {
            AboutPage(onNavigateBack = { drawerNavigator.goBack() }, onNavigateToLibraries = {
                drawerNavigator.navigate(SettingsKeys.Libraries)
            })
        }
        entry<SettingsKeys.Libraries>(
            metadata = sharedAxisXTransition()
        ) {
            LibrariesPage(
                onNavigateBack = { drawerNavigator.goBack() })
        }
        entry<SettingsKeys.Devices>(
            metadata = sharedAxisXTransition()
        ) {
            DevicesPage(
                onNavigateBack = {
                    drawerNavigator.goBack()
                })
        }
        entry<SettingsKeys.Search>(
            metadata = sharedAxisXTransition()
        ) {
            SearchSettingsPage(
                onNavigateBack = {
                    drawerNavigator.goBack()
                })
        }
        entry<SettingsKeys.RemoteControl>(metadata = sharedAxisXTransition()) {
            RemoteControlSettingsPage(onNavigateBack = {
                drawerNavigator.goBack()
            })
        }
    }

    val drawerContent = remember {
        movableContentOf {
            DrawerContent(
                currentDevice = currentDevice,
                devices = devices,
                scrollState = drawerScrollState,
                connectionState = connectionState,
                currentTopLevelRoute = drawerNavigationState.topLevelKey,
                onNavigate = { route ->
                    if (isSmallScreenLayout) {
                        scope.launch { modalDrawerState.close() }
                    }
                    drawerNavigator.navigate(route)
                },
                onCheckConnection = {
                    mainViewModel.checkConnection(true)
                },
                onOpenOwif = {
                    scope.launch { IntentUtils.openOwif(context, mainViewModel.buildOwifUrl()) }
                },
                onSetCurrentDevice = mainViewModel::setCurrentDevice
            )
        }
    }

    if (isSmallScreenLayout) {
        ModalNavigationDrawer(
            drawerState = modalDrawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerState = modalDrawerState,
                    modifier = Modifier.consumeWindowInsets(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Vertical + WindowInsetsSides.Start
                        )
                    ),
                ) {
                    drawerContent()
                }
            },
        ) {
            NavDisplay(
                entries = drawerNavigationState.toEntries(entryProvider),
                onBack = { drawerNavigator.goBack() },
                sceneStrategies = remember { listOf(DialogSceneStrategy()) })
        }
    } else {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet(
                    Modifier.consumeWindowInsets(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Vertical + WindowInsetsSides.Start
                        )
                    ),
                ) {
                    drawerContent()
                }
            }) {
            NavDisplay(
                entries = drawerNavigationState.toEntries(entryProvider),
                onBack = { drawerNavigator.goBack() },
                sceneStrategies = remember { listOf(DialogSceneStrategy()) })
        }
    }
}