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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import io.github.deprec8.enigmadroid.common.constant.MainKeys
import io.github.deprec8.enigmadroid.common.constant.SettingsKeys
import io.github.deprec8.enigmadroid.ui.components.isSmallScreenLayout
import io.github.deprec8.enigmadroid.ui.components.navigation.DrawerNavigator
import io.github.deprec8.enigmadroid.ui.components.navigation.fadeThroughTransition
import io.github.deprec8.enigmadroid.ui.components.navigation.rememberDrawerNavigationState
import io.github.deprec8.enigmadroid.ui.components.navigation.sharedAxisXTransition
import io.github.deprec8.enigmadroid.ui.components.navigation.toEntries
import io.github.deprec8.enigmadroid.ui.current.CurrentPage
import io.github.deprec8.enigmadroid.ui.deviceinfo.DeviceInfoPage
import io.github.deprec8.enigmadroid.ui.epg.radio.RadioEpgPage
import io.github.deprec8.enigmadroid.ui.epg.service.ServiceEpgPage
import io.github.deprec8.enigmadroid.ui.epg.service.ServiceEpgViewModel
import io.github.deprec8.enigmadroid.ui.epg.tv.TvEpgPage
import io.github.deprec8.enigmadroid.ui.live.radio.RadioPage
import io.github.deprec8.enigmadroid.ui.live.tv.TvPage
import io.github.deprec8.enigmadroid.ui.movies.MoviesDirectoryPage
import io.github.deprec8.enigmadroid.ui.movies.MoviesPage
import io.github.deprec8.enigmadroid.ui.movies.MoviesViewModel
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

@Composable
fun MainNavigationDisplay(
    onNavigateToRemoteControl: () -> Unit, mainViewModel: MainViewModel = hiltViewModel()
) {
    val currentDevice by mainViewModel.currentDevice.collectAsStateWithLifecycle()
    val loadingState by mainViewModel.loadingState.collectAsStateWithLifecycle()

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
            TvPage(
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
        entry<MainKeys.ServiceEpg>(
            metadata = sharedAxisXTransition()
        ) { backStackEntry ->
            val serviceEpgViewModel: ServiceEpgViewModel = hiltViewModel()
            LaunchedEffect(backStackEntry) {
                serviceEpgViewModel.initialize(backStackEntry.serviceReference)
            }
            ServiceEpgPage(
                serviceName = backStackEntry.serviceName,
                onNavigateBack = { drawerNavigator.goBack() },
                serviceEpgViewModel
            )
        }
        entry<MainKeys.Movies>(
            metadata = fadeThroughTransition()
        ) {
            MoviesPage(
                onNavigateToRemoteControl = { onNavigateToRemoteControl() },
                onNavigateToDirectory = { path, preloadBatch ->
                    drawerNavigator.navigate(MainKeys.MoviesDirectory(path, preloadBatch))
                },
                modalDrawerState
            )
        }
        entry<MainKeys.MoviesDirectory>(
            metadata = sharedAxisXTransition()
        ) { backStackEntry ->
            val moviesViewModel: MoviesViewModel = hiltViewModel()
            LaunchedEffect(backStackEntry) {
                moviesViewModel.initialize(backStackEntry.path, backStackEntry.preloadBatch)
            }
            MoviesDirectoryPage(
                onNavigateToRemoteControl = { onNavigateToRemoteControl() },
                onNavigateToDirectory = { path, preloadBatch ->
                    drawerNavigator.navigate(MainKeys.MoviesDirectory(path, preloadBatch))
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
        entry<MainKeys.TvEpg>(
            metadata = fadeThroughTransition()
        ) {
            TvEpgPage(
                onNavigateToRemoteControl = { onNavigateToRemoteControl() }, modalDrawerState
            )
        }
        entry<MainKeys.RadioEpg>(
            metadata = fadeThroughTransition()
        ) {
            RadioEpgPage(
                onNavigateToRemoteControl = { onNavigateToRemoteControl() }, modalDrawerState
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
        entry<MainKeys.Radio>(
            metadata = fadeThroughTransition()
        ) {
            RadioPage(
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
        entry<MainKeys.Timers>(
            metadata = fadeThroughTransition()
        ) {
            TimersPage(
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
        entry<MainKeys.DeviceInfo>(
            metadata = fadeThroughTransition()
        ) {
            DeviceInfoPage(
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
                scrollState = drawerScrollState,
                loadingState = loadingState,
                currentTopLevelRoute = drawerNavigationState.topLevelKey,
                onNavigate = { route ->
                    if (isSmallScreenLayout) {
                        scope.launch { modalDrawerState.close() }
                    }
                    drawerNavigator.navigate(route)
                },
                onUpdateDeviceStatus = {
                    scope.launch { mainViewModel.updateLoadingState(true) }
                },
                onOpenOwif = {
                    scope.launch { IntentUtils.openOwif(context, mainViewModel.buildOwifUrl()) }
                },
            )
        }
    }

    val navigationDisplay = remember {
        movableContentOf {
            NavDisplay(
                entries = drawerNavigationState.toEntries(entryProvider),
                onBack = { drawerNavigator.goBack() },
                sceneStrategies = remember { listOf(DialogSceneStrategy()) })
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
            navigationDisplay()
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
            navigationDisplay()
        }
    }
}