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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import io.github.deprec8.enigmadroid.model.navigation.MainPages
import io.github.deprec8.enigmadroid.model.navigation.SettingsPages
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
import io.github.deprec8.enigmadroid.ui.signal.SignalPage
import io.github.deprec8.enigmadroid.ui.timers.TimersPage

@Composable
fun NavHost(
    navHostController: NavHostController, drawerState: DrawerState
) {
    Surface {
        NavHost(
            navController = navHostController, startDestination = MainPages.TV,
            enterTransition = { fadeIn() }, exitTransition = { fadeOut() },
        ) {

            composable<MainPages.TV> {
                TvPage(
                    onNavigateToRemoteControl = { navHostController.navigate(MainPages.RemoteControl) },
                    onNavigateToServiceEpg = { serviceReference, serviceName ->
                        navHostController.navigate(
                            MainPages.ServiceEpg(
                                serviceReference, serviceName
                            )
                        )
                    },
                    drawerState
                )
            }
            composable<MainPages.ServiceEpg> { backStackEntry ->
                val serviceEpg: MainPages.ServiceEpg = backStackEntry.toRoute()
                ServiceEpgPage(
                    serviceReference = serviceEpg.serviceReference,
                    serviceName = serviceEpg.serviceName,
                    onNavigateBack = { navHostController.navigateUp() })
            }
            composable<MainPages.Movies> {
                MoviesPage(
                    onNavigateToRemoteControl = { navHostController.navigate(MainPages.RemoteControl) },
                    drawerState
                )
            }
            composable<MainPages.TvEpg> {
                TvEpgPage(
                    onNavigateToRemoteControl = { navHostController.navigate(MainPages.RemoteControl) },
                    drawerState
                )
            }
            composable<MainPages.RadioEpg> {
                RadioEpgPage(
                    onNavigateToRemoteControl = { navHostController.navigate(MainPages.RemoteControl) },
                    drawerState
                )
            }
            composable<MainPages.Current> {
                CurrentPage(
                    onNavigateToRemoteControl = { navHostController.navigate(MainPages.RemoteControl) },
                    onNavigateToServiceEpg = { serviceReference, serviceName ->
                        navHostController.navigate(
                            MainPages.ServiceEpg(
                                serviceReference, serviceName
                            )
                        )
                    },
                    drawerState = drawerState
                )
            }
            composable<MainPages.Radio> {
                RadioPage(
                    onNavigateToRemoteControl = { navHostController.navigate(MainPages.RemoteControl) },
                    onNavigateToServiceEpg = { serviceReference, serviceName ->
                        navHostController.navigate(
                            MainPages.ServiceEpg(
                                serviceReference, serviceName
                            )
                        )
                    },
                    drawerState
                )
            }
            composable<MainPages.Timers> {
                TimersPage(
                    onNavigateToRemoteControl = { navHostController.navigate(MainPages.RemoteControl) },
                    drawerState
                )
            }

            composable<MainPages.Signal> {
                SignalPage(
                    onNavigateToRemoteControl = { navHostController.navigate(MainPages.RemoteControl) },
                    drawerState
                )
            }
            composable<MainPages.DeviceInfo> {
                DeviceInfoPage(
                    onNavigateToRemoteControl = { navHostController.navigate(MainPages.RemoteControl) },
                    drawerState
                )
            }
            composable<MainPages.Settings> {
                SettingsPage(
                    drawerState, onNavigateToSettingsPage = {
                        navHostController.navigate(it)
                    })
            }
            composable<SettingsPages.About> {
                AboutPage(
                    onNavigateBack = { navHostController.navigate(MainPages.Settings) },
                    onNavigateToLibraries = {
                        navHostController.navigate(SettingsPages.Libraries)
                    })
            }
            composable<SettingsPages.Libraries> {
                LibrariesPage(
                    onNavigateBack = { navHostController.navigate(SettingsPages.About) })
            }
            composable<SettingsPages.Devices> {
                DevicesPage(
                    onNavigateBack = {
                        navHostController.navigate(MainPages.Settings)
                    })
            }
            composable<SettingsPages.RemoteControl> {
                RemoteControlSettingsPage(
                    onNavigateBack = {
                        navHostController.navigate(MainPages.Settings)
                    })
            }
            composable<SettingsPages.Search> {
                SearchSettingsPage(
                    onNavigateBack = {
                        navHostController.navigate(MainPages.Settings)
                    })
            }
            dialog<MainPages.RemoteControl>(
                deepLinks = listOf(navDeepLink {
                    uriPattern = "enigmadroid://remotecontrol"
                }), dialogProperties = DialogProperties(
                    dismissOnClickOutside = false,
                    decorFitsSystemWindows = false,
                    usePlatformDefaultWidth = false
                )
            ) {
                RemoteControlPage(onNavigateBack = {
                    navHostController.navigateUp()
                })
            }
        }
    }
}