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

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import io.github.deprec8.enigmadroid.model.navigation.MainPages
import io.github.deprec8.enigmadroid.model.navigation.SettingsPages
import io.github.deprec8.enigmadroid.ui.components.navigation.NavigationState
import io.github.deprec8.enigmadroid.ui.components.navigation.Navigator
import io.github.deprec8.enigmadroid.ui.components.navigation.toEntries
import io.github.deprec8.enigmadroid.ui.current.CurrentPage
import io.github.deprec8.enigmadroid.ui.deviceInfo.DeviceInfoPage
import io.github.deprec8.enigmadroid.ui.epg.radioEpg.RadioEpgPage
import io.github.deprec8.enigmadroid.ui.epg.serviceEpg.ServiceEpgPage
import io.github.deprec8.enigmadroid.ui.epg.tvEpg.TvEpgPage
import io.github.deprec8.enigmadroid.ui.live.radio.RadioPage
import io.github.deprec8.enigmadroid.ui.live.tv.TvPage
import io.github.deprec8.enigmadroid.ui.movies.MoviesPage
import io.github.deprec8.enigmadroid.ui.remoteControl.RemoteControlPage
import io.github.deprec8.enigmadroid.ui.settings.SettingsPage
import io.github.deprec8.enigmadroid.ui.settings.about.AboutPage
import io.github.deprec8.enigmadroid.ui.settings.about.LibrariesPage
import io.github.deprec8.enigmadroid.ui.settings.devices.DevicesPage
import io.github.deprec8.enigmadroid.ui.settings.remoteControl.RemoteControlSettingsPage
import io.github.deprec8.enigmadroid.ui.settings.search.SearchSettingsPage
import io.github.deprec8.enigmadroid.ui.settings.stream.StreamSettingsPage
import io.github.deprec8.enigmadroid.ui.signal.SignalPage
import io.github.deprec8.enigmadroid.ui.timers.TimersPage

@Composable
fun NavigationDisplay(
    navigator: Navigator, navigationState: NavigationState, drawerState: DrawerState
) {
    val entryProvider = entryProvider {
        entry<MainPages.Tv> {
            TvPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                onNavigateToServiceEpg = { serviceReference, serviceName ->
                    navigator.navigate(
                        MainPages.ServiceEpg(
                            serviceReference, serviceName
                        )
                    )
                },
                drawerState
            )
        }
        entry<MainPages.ServiceEpg> { backStackEntry ->
            ServiceEpgPage(
                serviceReference = backStackEntry.serviceReference,
                serviceName = backStackEntry.serviceName,
                onNavigateBack = { navigator.goBack() })
        }
        entry<MainPages.Movies> {
            MoviesPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                drawerState
            )
        }
        entry<MainPages.TvEpg> {
            TvEpgPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                drawerState
            )
        }
        entry<MainPages.RadioEpg> {
            RadioEpgPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                drawerState
            )
        }
        entry<MainPages.Current> {
            CurrentPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                onNavigateToServiceEpg = { serviceReference, serviceName ->
                    navigator.navigate(
                        MainPages.ServiceEpg(
                            serviceReference, serviceName
                        )
                    )
                },
                drawerState = drawerState
            )
        }
        entry<MainPages.Radio> {
            RadioPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                onNavigateToServiceEpg = { serviceReference, serviceName ->
                    navigator.navigate(
                        MainPages.ServiceEpg(
                            serviceReference, serviceName
                        )
                    )
                },
                drawerState
            )
        }
        entry<MainPages.Timers> {
            TimersPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                drawerState
            )
        }
        entry<MainPages.Signal> {
            SignalPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                drawerState
            )
        }
        entry<MainPages.DeviceInfo> {
            DeviceInfoPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                drawerState
            )
        }
        entry<MainPages.Settings> {
            SettingsPage(
                drawerState, onNavigateToSettingsPage = {
                    navigator.navigate(it)
                })
        }
        entry<SettingsPages.About> {
            AboutPage(
                onNavigateBack = { navigator.goBack() },
                onNavigateToLibraries = {
                    navigator.navigate(SettingsPages.Libraries)
                })
        }
        entry<SettingsPages.Libraries> {
            LibrariesPage(
                onNavigateBack = { navigator.goBack() })
        }
        entry<SettingsPages.Devices> {
            DevicesPage(
                onNavigateBack = {
                    navigator.goBack()
                })
        }
        entry<SettingsPages.RemoteControl> {
            RemoteControlSettingsPage(
                onNavigateBack = {
                    navigator.goBack()
                })
        }
        entry<SettingsPages.Search> {
            SearchSettingsPage(
                onNavigateBack = {
                    navigator.goBack()
                })
        }
        entry<SettingsPages.Stream> {
            StreamSettingsPage(
                onNavigateBack = {
                    navigator.goBack()
                })
        }
        entry<MainPages.RemoteControl>(
            metadata = DialogSceneStrategy.dialog(
                DialogProperties(
                    dismissOnClickOutside = false,
                    decorFitsSystemWindows = false,
                    usePlatformDefaultWidth = false
                )
            ),
        ) {
            RemoteControlPage(onNavigateBack = {
                navigator.goBack()
            })
        }
    }

    Surface {
        NavDisplay(
            entries = navigationState.toEntries(entryProvider),
            onBack = { navigator.goBack() },
            sceneStrategy = remember { DialogSceneStrategy() },
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            popTransitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            predictivePopTransitionSpec = {
                fadeIn() togetherWith fadeOut()
            })
    }
}