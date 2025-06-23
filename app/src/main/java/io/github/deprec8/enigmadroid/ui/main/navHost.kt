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

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navDeepLink
import io.github.deprec8.enigmadroid.model.navigation.MainPages
import io.github.deprec8.enigmadroid.model.navigation.SettingsPages
import io.github.deprec8.enigmadroid.ui.current.CurrentPage
import io.github.deprec8.enigmadroid.ui.deviceInfo.DInfoPage
import io.github.deprec8.enigmadroid.ui.movies.MoviesPage
import io.github.deprec8.enigmadroid.ui.radio.RadioPage
import io.github.deprec8.enigmadroid.ui.radioEPG.RadioEPGPage
import io.github.deprec8.enigmadroid.ui.remote.RemotePage
import io.github.deprec8.enigmadroid.ui.settings.SettingsPage
import io.github.deprec8.enigmadroid.ui.settings.about.AboutPage
import io.github.deprec8.enigmadroid.ui.settings.devices.DevicesPage
import io.github.deprec8.enigmadroid.ui.signal.SignalPage
import io.github.deprec8.enigmadroid.ui.timers.TimersPage
import io.github.deprec8.enigmadroid.ui.tv.TvPage
import io.github.deprec8.enigmadroid.ui.tvEPG.TVEPGPage

@Composable
fun NavHost(
    navController: NavHostController,
    modalDrawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
) {
    Surface {
        NavHost(
            navController = navController, startDestination = MainPages.TV,
            enterTransition = { fadeIn() }, exitTransition = { fadeOut() },
        ) {

            composable<MainPages.TV> {
                TvPage(
                    onNavigateToRemote = { navController.navigate(MainPages.Remote) },
                    snackbarHostState,
                    modalDrawerState
                )
            }
            composable<MainPages.Movies> {
                MoviesPage(
                    onNavigateToRemote = { navController.navigate(MainPages.Remote) },
                    snackbarHostState,
                    modalDrawerState
                )
            }
            composable<MainPages.TVEPG> {
                TVEPGPage(
                    onNavigateToRemote = { navController.navigate(MainPages.Remote) },
                    snackbarHostState,
                    modalDrawerState
                )
            }
            composable<MainPages.RadioEPG> {
                RadioEPGPage(
                    onNavigateToRemote = { navController.navigate(MainPages.Remote) },
                    snackbarHostState,
                    modalDrawerState
                )
            }
            composable<MainPages.Current> {
                CurrentPage(
                    onNavigateToRemote = { navController.navigate(MainPages.Remote) },
                    snackbarHostState = snackbarHostState,
                    drawerState = modalDrawerState
                )
            }
            composable<MainPages.Radio> {
                RadioPage(
                    onNavigateToRemote = { navController.navigate(MainPages.Remote) },
                    snackbarHostState,
                    modalDrawerState
                )
            }
            composable<MainPages.Timers> {
                TimersPage(
                    onNavigateToRemote = { navController.navigate(MainPages.Remote) },
                    snackbarHostState,
                    modalDrawerState
                )
            }

            composable<MainPages.Signal> {
                SignalPage(
                    onNavigateToRemote = { navController.navigate(MainPages.Remote) },
                    snackbarHostState,
                    modalDrawerState
                )
            }
            composable<MainPages.DeviceInfo> {
                DInfoPage(
                    onNavigateToRemote = { navController.navigate(MainPages.Remote) },
                    snackbarHostState,
                    modalDrawerState
                )
            }
            composable<MainPages.Settings> {
                SettingsPage(
                    modalDrawerState,
                    snackbarHostState,
                    onNavigateToSubPage = {
                        navController.navigate(it)
                    }
                )
            }
            composable<SettingsPages.About> {
                AboutPage(
                    snackbarHostState,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            composable<SettingsPages.Devices> {
                DevicesPage(
                    snackbarHostState,
                    onNavigateBack = {
                        navController.navigateUp()
                    }
                )
            }

            dialog<MainPages.Remote>(
                deepLinks = listOf(navDeepLink {
                    uriPattern = "enigmadroid://remote"
                }),
                dialogProperties = DialogProperties(
                    dismissOnClickOutside = false,
                    decorFitsSystemWindows = false,
                    usePlatformDefaultWidth = false
                )
            ) {
                RemotePage(onNavigateBack = {
                    navController.navigateUp()
                }, snackbarHostState)
            }
        }
    }
}