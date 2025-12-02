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

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import io.github.deprec8.enigmadroid.ui.onboarding.OnboardingPage
import kotlinx.coroutines.launch

@Composable
fun MainPage(
    mainViewModel: MainViewModel = hiltViewModel(),
) {

    val currentDevice by mainViewModel.currentDevice.collectAsStateWithLifecycle()
    val loadingState by mainViewModel.loadingState.collectAsStateWithLifecycle()
    val isOnboardingNeeded by mainViewModel.isOnboardingNeeded.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val modalDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val scope = rememberCoroutineScope()

    LaunchedEffect(
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) &&
                windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
    ) {
        modalDrawerState.close()
    }

    if (isOnboardingNeeded) {
        OnboardingPage()
    } else {
        if (! windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) ||
            ! windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
        ) {
            ModalNavigationDrawer(
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier
                            .consumeWindowInsets(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Vertical
                                )
                            )
                            .consumeWindowInsets(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Start
                                )
                            )
                    ) {
                        NavDrawerContent(
                            currentDevice = currentDevice,
                            navController = navController,
                            modalDrawerState = modalDrawerState,
                            updateDeviceStatus = {
                                scope.launch {
                                    mainViewModel.updateLoadingState(
                                        true
                                    )
                                }
                            },
                            makeOWIFURL = mainViewModel::makeOWIFURL,
                            loadingState = loadingState
                        )
                    }
                },
                drawerState = modalDrawerState
            ) {
                NavHost(
                    navController,
                    modalDrawerState
                )
            }
        } else {
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(
                        Modifier
                            .consumeWindowInsets(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Vertical
                                )
                            )
                            .consumeWindowInsets(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Start
                                )
                            )
                    ) {
                        NavDrawerContent(
                            currentDevice = currentDevice,
                            navController = navController,
                            modalDrawerState = modalDrawerState,
                            updateDeviceStatus = {
                                scope.launch {
                                    mainViewModel.updateLoadingState(
                                        true
                                    )
                                }
                            },
                            makeOWIFURL = mainViewModel::makeOWIFURL,
                            loadingState = loadingState
                        )
                    }
                }
            ) {
                NavHost(
                    navController, modalDrawerState
                )
            }
        }
    }
}