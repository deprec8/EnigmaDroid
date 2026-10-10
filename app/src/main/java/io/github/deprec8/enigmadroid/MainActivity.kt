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

package io.github.deprec8.enigmadroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import io.github.deprec8.enigmadroid.constant.IntentKeys
import io.github.deprec8.enigmadroid.data.repositories.DevicesRepository
import io.github.deprec8.enigmadroid.data.repositories.OnboardingRepository
import io.github.deprec8.enigmadroid.ui.root.RootNavigationDisplay
import io.github.deprec8.enigmadroid.ui.theme.EnigmaDroidTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {

    private val devicesRepository: DevicesRepository by inject()
    private val onboardingRepository: OnboardingRepository by inject()

    private var isSetupFinished = false

    private var isOnboardingNeeded by mutableStateOf(false)
    private var isRemoteControlDeepLink by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            !isSetupFinished
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        processIntent(intent)
        setContent {
            EnigmaDroidTheme {
                RootNavigationDisplay(
                    isOnboardingNeeded, isRemoteControlDeepLink
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        processIntent(intent)
    }

    private fun processIntent(intent: Intent?) {
        lifecycleScope.launch {
            isOnboardingNeeded = onboardingRepository.onboardingNeeded.first()
            if (!isOnboardingNeeded && intent != null) {
                when (intent.action) {
                    Intent.ACTION_VIEW -> isRemoteControlDeepLink =
                        intent.data?.toString() == "enigmadroid://remotecontrol"

                    IntentKeys.OPEN_WITH_DEVICE_ACTION -> intent.getLongExtra(
                        IntentKeys.DEVICE_ID_EXTRA, -1L
                    ).let { id ->
                        devicesRepository.setCurrentId(id)
                    }
                }
            } else {
                isRemoteControlDeepLink = false
            }
            isSetupFinished = true
        }
    }
}