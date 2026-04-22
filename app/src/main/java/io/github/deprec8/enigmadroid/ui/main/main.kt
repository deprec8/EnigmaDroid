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

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass
import io.github.deprec8.enigmadroid.model.navigation.MainPages
import io.github.deprec8.enigmadroid.ui.components.navigation.DrawerSceneDecoratorStrategy
import io.github.deprec8.enigmadroid.ui.components.navigation.LocalSharedTransitionScope
import io.github.deprec8.enigmadroid.ui.components.navigation.ModalNavigationDrawerWrapper
import io.github.deprec8.enigmadroid.ui.components.navigation.Navigator
import io.github.deprec8.enigmadroid.ui.components.navigation.rememberNavigationState
import io.github.deprec8.enigmadroid.ui.onboarding.OnboardingPage
import io.github.deprec8.enigmadroid.utils.IntentUtils
import kotlinx.coroutines.launch

@Composable
fun MainPage(
    isRemoteControlDeepLink: Boolean,
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val currentDevice by mainViewModel.currentDevice.collectAsStateWithLifecycle()
    val loadingState by mainViewModel.loadingState.collectAsStateWithLifecycle()
    val isOnboardingNeeded by mainViewModel.isOnboardingNeeded.collectAsStateWithLifecycle()

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isSmallScreenLayout =
        ! windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) || ! windowSizeClass.isHeightAtLeastBreakpoint(
            WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND
        )

    val navigationState = rememberNavigationState(
        startRoute = MainPages.Tv,
        topLevelRoutes = setOf(
            MainPages.Tv,
            MainPages.Radio,
            MainPages.Current,
            MainPages.Movies,
            MainPages.Timers,
            MainPages.TvEpg,
            MainPages.RadioEpg,
            MainPages.DeviceInfo,
            MainPages.Signal,
            MainPages.Settings,
        ),
        deepLinkRoute = MainPages.RemoteControl.takeIf { isRemoteControlDeepLink },
    )

    val navigator = remember(navigationState) { Navigator(navigationState) }

    val modalDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val drawerScrollState = rememberScrollState()

    val isModalDrawerEnabled by remember(
        navigationState.topLevelRoute, navigationState.backStacks, isSmallScreenLayout
    ) {
        derivedStateOf {
            navigationState.backStacks[navigationState.topLevelRoute]?.lastOrNull()?.let {
                it != MainPages.RemoteControl
            } == true && isSmallScreenLayout
        }
    }

    val drawerContent = remember {
        movableContentOf {
            NavDrawerContent(
                currentDevice = currentDevice,
                scrollState = drawerScrollState,
                loadingState = loadingState,
                currentTopLevelRoute = navigationState.topLevelRoute,
                onNavigate = { route ->
                    if (isSmallScreenLayout) {
                        scope.launch { modalDrawerState.close() }
                    }
                    navigator.navigate(route)
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

    val drawerSceneDecoratorStrategy = remember(isSmallScreenLayout) {
        DrawerSceneDecoratorStrategy<NavKey>(
            isSmallScreenLayout = isSmallScreenLayout,
            drawerContent = drawerContent,
        )
    }

    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            if (isOnboardingNeeded) {
                OnboardingPage()
            } else {
                ModalNavigationDrawerWrapper(
                    enabled = isModalDrawerEnabled,
                    drawerState = modalDrawerState,
                    drawerContent = drawerContent,
                ) {
                    NavigationDisplay(navigator, modalDrawerState, drawerSceneDecoratorStrategy)
                }
            }
        }
    }
}