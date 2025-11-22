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

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import io.github.deprec8.enigmadroid.model.navigation.MainPages
import io.github.deprec8.enigmadroid.model.navigation.SettingsPages
import io.github.deprec8.enigmadroid.ui.components.Navigator
import io.github.deprec8.enigmadroid.ui.components.toEntries
import io.github.deprec8.enigmadroid.ui.current.CurrentPage
import io.github.deprec8.enigmadroid.ui.deviceInfo.DInfoPage
import io.github.deprec8.enigmadroid.ui.movies.MoviesPage
import io.github.deprec8.enigmadroid.ui.radio.RadioPage
import io.github.deprec8.enigmadroid.ui.radioEPG.RadioEPGPage
import io.github.deprec8.enigmadroid.ui.remoteControl.RemoteControlPage
import io.github.deprec8.enigmadroid.ui.settings.SettingsPage
import io.github.deprec8.enigmadroid.ui.settings.about.AboutPage
import io.github.deprec8.enigmadroid.ui.settings.about.LibrariesPage
import io.github.deprec8.enigmadroid.ui.settings.devices.DevicesPage
import io.github.deprec8.enigmadroid.ui.settings.remoteControl.RemoteControlSettingsPage
import io.github.deprec8.enigmadroid.ui.settings.search.SearchSettingsPage
import io.github.deprec8.enigmadroid.ui.signal.SignalPage
import io.github.deprec8.enigmadroid.ui.timers.TimersPage
import io.github.deprec8.enigmadroid.ui.tv.TvPage
import io.github.deprec8.enigmadroid.ui.tvEPG.TVEPGPage

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavHost(
    navigator: Navigator,
    modalDrawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
) {
    val entryProvider = entryProvider {
        entry<MainPages.TV> {
            TvPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                snackbarHostState,
                modalDrawerState
            )
        }
        entry<MainPages.Movies> {
            MoviesPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                snackbarHostState,
                modalDrawerState
            )
        }
        entry<MainPages.TVEPG> {
            TVEPGPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                snackbarHostState,
                modalDrawerState
            )
        }
        entry<MainPages.RadioEPG> {
            RadioEPGPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                snackbarHostState,
                modalDrawerState
            )
        }
        entry<MainPages.Current> {
            CurrentPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                snackbarHostState = snackbarHostState,
                drawerState = modalDrawerState
            )
        }
        entry<MainPages.Radio> {
            RadioPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                snackbarHostState,
                modalDrawerState
            )
        }
        entry<MainPages.Timers> {
            TimersPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                snackbarHostState,
                modalDrawerState
            )
        }
        entry<MainPages.Signal> {
            SignalPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                snackbarHostState,
                modalDrawerState
            )
        }
        entry<MainPages.DeviceInfo> {
            DInfoPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                snackbarHostState,
                modalDrawerState
            )
        }
        entry<MainPages.Settings> {
            SettingsPage(
                modalDrawerState,
                snackbarHostState,
                onNavigateToSubPage = {
                    navigator.navigate(it)
                }
            )
        }
        entry<SettingsPages.About> {
            AboutPage(
                snackbarHostState,
                onNavigateBack = { navigator.navigate(MainPages.Settings) },
                onNavigateToLibraries = {
                    navigator.navigate(SettingsPages.Libraries)
                }
            )
        }
        entry<SettingsPages.Libraries> {
            LibrariesPage(
                snackbarHostState,
                onNavigateBack = { navigator.navigate(SettingsPages.About) }
            )
        }
        entry<SettingsPages.Devices> {
            DevicesPage(
                snackbarHostState,
                onNavigateBack = {
                    navigator.navigate(MainPages.Settings)
                }
            )
        }
        entry<SettingsPages.RemoteControl> {
            RemoteControlSettingsPage(
                snackbarHostState,
                onNavigateBack = {
                    navigator.navigate(MainPages.Settings)
                }
            )
        }
        entry<SettingsPages.Search> {
            SearchSettingsPage(
                snackbarHostState,
                onNavigateBack = {
                    navigator.navigate(MainPages.Settings)
                }
            )
        }
        entry<MainPages.RemoteControl>(
            metadata = DialogSceneStrategy.dialog(
                dialogProperties = DialogProperties(
                    dismissOnClickOutside = false,
                    decorFitsSystemWindows = false,
                    usePlatformDefaultWidth = false
                )
            )
        ) {
            RemoteControlPage(onNavigateBack = {
                navigator.goBack()
            }, snackbarHostState)
        }
    }

    Surface {
        NavDisplay(
            entries = navigator.state.toEntries(entryProvider),
            onBack = { navigator.goBack() },
            sceneStrategy = remember { DialogSceneStrategy() },
            predictivePopTransitionSpec = {
                fadeIn().togetherWith(fadeOut())
            },
            popTransitionSpec = {
                fadeIn().togetherWith(fadeOut())
            }
        )
    }
}