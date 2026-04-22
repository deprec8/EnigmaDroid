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

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import io.github.deprec8.enigmadroid.model.navigation.MainPages
import io.github.deprec8.enigmadroid.model.navigation.SettingsPages
import io.github.deprec8.enigmadroid.ui.components.navigation.DrawerSceneDecoratorStrategy
import io.github.deprec8.enigmadroid.ui.components.navigation.LocalSharedTransitionScope
import io.github.deprec8.enigmadroid.ui.components.navigation.Navigator
import io.github.deprec8.enigmadroid.ui.components.navigation.toEntries
import io.github.deprec8.enigmadroid.ui.current.CurrentPage
import io.github.deprec8.enigmadroid.ui.deviceInfo.DeviceInfoPage
import io.github.deprec8.enigmadroid.ui.epg.radioEpg.RadioEpgPage
import io.github.deprec8.enigmadroid.ui.epg.serviceEpg.ServiceEpgPage
import io.github.deprec8.enigmadroid.ui.epg.serviceEpg.ServiceEpgViewModel
import io.github.deprec8.enigmadroid.ui.epg.tvEpg.TvEpgPage
import io.github.deprec8.enigmadroid.ui.live.radio.RadioPage
import io.github.deprec8.enigmadroid.ui.live.tv.TvPage
import io.github.deprec8.enigmadroid.ui.movies.MoviesDirectoryPage
import io.github.deprec8.enigmadroid.ui.movies.MoviesPage
import io.github.deprec8.enigmadroid.ui.movies.MoviesViewModel
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
fun NavigationDisplay(
    navigator: Navigator,
    drawerState: DrawerState,
    drawerSceneDecoratorStrategy: DrawerSceneDecoratorStrategy<NavKey>
) {
    val density = LocalDensity.current
    val slideDistance = remember(density) { with(density) { 30.dp.roundToPx() } }

    val sharedAxisXForward: ContentTransform = (slideInHorizontally(
        animationSpec = tween(300), initialOffsetX = { slideDistance }) + fadeIn(
        animationSpec = tween(300)
    )) togetherWith (slideOutHorizontally(
        animationSpec = tween(300), targetOffsetX = { - slideDistance }) + fadeOut(
        animationSpec = tween(300)
    ))

    val sharedAxisXBackward: ContentTransform = (slideInHorizontally(
        animationSpec = tween(300), initialOffsetX = { - slideDistance }) + fadeIn(
        animationSpec = tween(300)
    )) togetherWith (slideOutHorizontally(
        animationSpec = tween(300), targetOffsetX = { slideDistance }) + fadeOut(
        animationSpec = tween(300)
    ))

    val fadeThrough: ContentTransform =
        (fadeIn(animationSpec = tween(300))) togetherWith (fadeOut(animationSpec = tween(300)))

    val fadeThroughTransition = metadata {
        put(NavDisplay.TransitionKey) {
            fadeThrough
        }
        put(NavDisplay.PopTransitionKey) {
            fadeThrough
        }
        put(NavDisplay.PredictivePopTransitionKey) {
            fadeThrough
        }
    }

    val sharedAxisXTransition = metadata {
        put(NavDisplay.TransitionKey) {
            sharedAxisXForward
        }
        put(NavDisplay.PopTransitionKey) {
            sharedAxisXBackward
        }
        put(NavDisplay.PredictivePopTransitionKey) {
            sharedAxisXBackward
        }
    }

    val entryProvider = entryProvider {
        entry<MainPages.Tv>(
            metadata = DrawerSceneDecoratorStrategy.drawerScene() + fadeThroughTransition
        ) {
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
        entry<MainPages.ServiceEpg>(
            metadata = DrawerSceneDecoratorStrategy.drawerScene() + sharedAxisXTransition
        ) { backStackEntry ->
            val serviceEpgViewModel: ServiceEpgViewModel = hiltViewModel()
            LaunchedEffect(backStackEntry) {
                serviceEpgViewModel.initialize(backStackEntry.serviceReference)
            }
            ServiceEpgPage(
                serviceName = backStackEntry.serviceName,
                onNavigateBack = { navigator.goBack() },
                serviceEpgViewModel
            )
        }
        entry<MainPages.Movies>(
            metadata = DrawerSceneDecoratorStrategy.drawerScene() + fadeThroughTransition
        ) {
            MoviesPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                onNavigateToDirectory = { path, preloadBatch ->
                    navigator.navigate(MainPages.MoviesDirectory(path, preloadBatch))
                },
                drawerState
            )
        }
        entry<MainPages.MoviesDirectory>(
            metadata = DrawerSceneDecoratorStrategy.drawerScene() + sharedAxisXTransition
        ) { backStackEntry ->
            val moviesViewModel: MoviesViewModel = hiltViewModel()
            LaunchedEffect(backStackEntry) {
                moviesViewModel.initialize(backStackEntry.path, backStackEntry.preloadBatch)
            }
            MoviesDirectoryPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                onNavigateToDirectory = { path, preloadBatch ->
                    navigator.navigate(MainPages.MoviesDirectory(path, preloadBatch))
                },
                onNavigateBack = {
                    navigator.goBack()
                },
                onNavigateToTop = {
                    navigator.goTop()
                },
                moviesViewModel
            )
        }
        entry<MainPages.TvEpg>(
            metadata = DrawerSceneDecoratorStrategy.drawerScene() + fadeThroughTransition
        ) {
            TvEpgPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                drawerState
            )
        }
        entry<MainPages.RadioEpg>(
            metadata = DrawerSceneDecoratorStrategy.drawerScene() + fadeThroughTransition
        ) {
            RadioEpgPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                drawerState
            )
        }
        entry<MainPages.Current>(
            metadata = DrawerSceneDecoratorStrategy.drawerScene() + fadeThroughTransition
        ) {
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
        entry<MainPages.Radio>(
            metadata = DrawerSceneDecoratorStrategy.drawerScene() + fadeThroughTransition
        ) {
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
        entry<MainPages.Timers>(
            metadata = DrawerSceneDecoratorStrategy.drawerScene() + fadeThroughTransition
        ) {
            TimersPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                drawerState
            )
        }
        entry<MainPages.Signal>(
            metadata = DrawerSceneDecoratorStrategy.drawerScene() + fadeThroughTransition
        ) {
            SignalPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                drawerState
            )
        }
        entry<MainPages.DeviceInfo>(
            metadata = DrawerSceneDecoratorStrategy.drawerScene() + fadeThroughTransition
        ) {
            DeviceInfoPage(
                onNavigateToRemoteControl = { navigator.navigate(MainPages.RemoteControl) },
                drawerState
            )
        }
        entry<MainPages.Settings>(
            metadata = DrawerSceneDecoratorStrategy.drawerScene() + fadeThroughTransition
        ) {
            SettingsPage(
                drawerState, onNavigateToSettingsPage = {
                    navigator.navigate(it)
                })
        }
        entry<SettingsPages.About>(
            metadata = DrawerSceneDecoratorStrategy.drawerScene() + sharedAxisXTransition
        ) {
            AboutPage(onNavigateBack = { navigator.goBack() }, onNavigateToLibraries = {
                navigator.navigate(SettingsPages.Libraries)
            })
        }
        entry<SettingsPages.Libraries>(
            metadata = DrawerSceneDecoratorStrategy.drawerScene() + sharedAxisXTransition
        ) {
            LibrariesPage(
                onNavigateBack = { navigator.goBack() })
        }
        entry<SettingsPages.Devices>(
            metadata = DrawerSceneDecoratorStrategy.drawerScene() + sharedAxisXTransition
        ) {
            DevicesPage(
                onNavigateBack = {
                    navigator.goBack()
                })
        }
        entry<SettingsPages.RemoteControl>(
            metadata = DrawerSceneDecoratorStrategy.drawerScene() + sharedAxisXTransition
        ) {
            RemoteControlSettingsPage(
                onNavigateBack = {
                    navigator.goBack()
                })
        }
        entry<SettingsPages.Search>(
            metadata = DrawerSceneDecoratorStrategy.drawerScene() + sharedAxisXTransition
        ) {
            SearchSettingsPage(
                onNavigateBack = {
                    navigator.goBack()
                })
        }
        entry<MainPages.RemoteControl>(metadata = sharedAxisXTransition) {
            RemoteControlPage(onNavigateBack = {
                navigator.goBack()
            })
        }
    }

    val sharedTransitionScope = LocalSharedTransitionScope.current

    Surface {
        NavDisplay(
            entries = navigator.state.toEntries(entryProvider),
            sharedTransitionScope = sharedTransitionScope,
            onBack = { navigator.goBack() },
            sceneStrategies = remember { listOf(DialogSceneStrategy()) },
            sceneDecoratorStrategies = listOf(drawerSceneDecoratorStrategy),
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