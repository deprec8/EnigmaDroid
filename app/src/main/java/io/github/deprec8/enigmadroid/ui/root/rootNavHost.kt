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

package io.github.deprec8.enigmadroid.ui.root

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import io.github.deprec8.enigmadroid.model.navigation.RootPages
import io.github.deprec8.enigmadroid.ui.main.MainPage
import io.github.deprec8.enigmadroid.ui.remote.RemotePage

@Composable
fun RootNavHost() {
    val rootNavController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    Surface {
        NavHost(
            navController = rootNavController, startDestination = RootPages.Main
        ) {
            composable<RootPages.Main> {
                MainPage(
                    snackbarHostState = snackbarHostState,
                    onNavigateToRemote = { rootNavController.navigate(RootPages.Remote) },
                )
            }

            composable<RootPages.Remote>(
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec =
                            spring(stiffness = 400f)
                    ) + fadeIn()
                }, exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec =
                            spring(stiffness = 400f)
                    ) + fadeOut()
                }, deepLinks = listOf(navDeepLink { uriPattern = "enigmadroid://remote" })
            ) {
                RemotePage(onNavigateBack = {
                    rootNavController.navigateUp()
                }, snackbarHostState)
            }
        }
    }
}