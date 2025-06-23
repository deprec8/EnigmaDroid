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
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.github.deprec8.enigmadroid.model.navigation.MainPages
import io.github.deprec8.enigmadroid.model.navigation.SettingsPages
import io.github.deprec8.enigmadroid.ui.settings.about.AboutPage
import io.github.deprec8.enigmadroid.ui.current.CurrentPage
import io.github.deprec8.enigmadroid.ui.deviceInfo.DInfoPage
import io.github.deprec8.enigmadroid.ui.movies.MoviesPage
import io.github.deprec8.enigmadroid.ui.radio.RadioPage
import io.github.deprec8.enigmadroid.ui.radioEPG.RadioEPGPage
import io.github.deprec8.enigmadroid.ui.settings.SettingsPage
import io.github.deprec8.enigmadroid.ui.settings.devices.DevicesPage
import io.github.deprec8.enigmadroid.ui.signal.SignalPage
import io.github.deprec8.enigmadroid.ui.timers.TimersPage
import io.github.deprec8.enigmadroid.ui.tv.TvPage
import io.github.deprec8.enigmadroid.ui.tvEPG.TVEPGPage

@Composable
fun MainNavHost(
    mainNavController: NavHostController,
    modalDrawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
    onNavigateToRemote: () -> Unit,
) {
    NavHost(
        navController = mainNavController, startDestination = MainPages.TV,
        enterTransition = { fadeIn() }, exitTransition = { fadeOut() },
    ) {
        // Content
        composable<MainPages.TV> {
            TvPage(
                onNavigateToRemote = { onNavigateToRemote() },
                snackbarHostState,
                modalDrawerState
            )
        }
        composable<MainPages.Movies> {
            MoviesPage(
                onNavigateToRemote = { onNavigateToRemote() },
                snackbarHostState,
                modalDrawerState
            )
        }
        composable<MainPages.TVEPG> {
            TVEPGPage(
                onNavigateToRemote = { onNavigateToRemote() },
                snackbarHostState,
                modalDrawerState
            )
        }
        composable<MainPages.RadioEPG> {
            RadioEPGPage(
                onNavigateToRemote = { onNavigateToRemote() },
                snackbarHostState,
                modalDrawerState
            )
        }
        composable<MainPages.Current> {
            CurrentPage(
                onNavigateToRemote = { onNavigateToRemote() },
                snackbarHostState = snackbarHostState,
                drawerState = modalDrawerState
            )
        }
        composable<MainPages.Radio> {
            RadioPage(
                onNavigateToRemote = { onNavigateToRemote() },
                snackbarHostState,
                modalDrawerState
            )
        }
        composable<MainPages.Timers> {
            TimersPage(
                onNavigateToRemote = { onNavigateToRemote() },
                snackbarHostState,
                modalDrawerState
            )
        }

        // Device
        composable<MainPages.Signal> {
            SignalPage(
                onNavigateToRemote = { onNavigateToRemote() },
                snackbarHostState,
                modalDrawerState
            )
        }
        composable<MainPages.DeviceInfo> {
            DInfoPage(
                onNavigateToRemote = { onNavigateToRemote() },
                snackbarHostState,
                modalDrawerState
            )
        }
        composable<MainPages.Settings> {
            SettingsPage(
                modalDrawerState,
                snackbarHostState,
                onNavigateToSubPage = {
                    mainNavController.navigate(it)
                }
            )
        }
        composable<SettingsPages.About> {
            AboutPage(
                snackbarHostState,
                onNavigateBack = { mainNavController.navigateUp() }
            )
        }
        composable<SettingsPages.Devices> {
            DevicesPage(
                snackbarHostState,
                onNavigateBack = {
                    mainNavController.navigateUp() }
            )
        }
    }
}