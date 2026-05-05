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

package io.github.deprec8.enigmadroid.ui.root

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import io.github.deprec8.enigmadroid.common.constant.RootKeys
import io.github.deprec8.enigmadroid.ui.components.navigation.fadeThroughTransition
import io.github.deprec8.enigmadroid.ui.components.navigation.sharedAxisXTransition
import io.github.deprec8.enigmadroid.ui.main.MainNavigationDisplay
import io.github.deprec8.enigmadroid.ui.onboarding.OnboardingPage
import io.github.deprec8.enigmadroid.ui.remotecontrol.RemoteControlPage

@Composable
fun RootNavigationDisplay(
    isOnboardingNeeded: Boolean, isRemoteControlDeepLink: Boolean
) {
    val initialElements = if (isOnboardingNeeded) {
        arrayOf(RootKeys.Onboarding)
    } else if (isRemoteControlDeepLink) {
        arrayOf(RootKeys.Main, RootKeys.RemoteControl)
    } else {
        arrayOf(RootKeys.Main)
    }

    val rootBackStack = rememberNavBackStack(*initialElements)

    Surface {
        NavDisplay(
            entryProvider = entryProvider {
                entry<RootKeys.Main>(metadata = fadeThroughTransition()) {
                    MainNavigationDisplay(
                        { rootBackStack.add(RootKeys.RemoteControl) })
                }
                entry<RootKeys.RemoteControl>(metadata = sharedAxisXTransition()) {
                    RemoteControlPage(onNavigateBack = {
                        rootBackStack.removeLastOrNull()
                    })
                }
                entry<RootKeys.Onboarding>(metadata = fadeThroughTransition()) {
                    OnboardingPage(onOnboardingFinished = {
                        rootBackStack.clear()
                        rootBackStack.add(RootKeys.Main)
                    })
                }
            },
            onBack = { rootBackStack.removeLastOrNull() },
            backStack = rootBackStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            sceneStrategies = remember { listOf(DialogSceneStrategy()) })
    }
}