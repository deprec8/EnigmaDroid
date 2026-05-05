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
import dagger.hilt.android.AndroidEntryPoint
import io.github.deprec8.enigmadroid.data.DevicesRepository
import io.github.deprec8.enigmadroid.data.OnboardingRepository
import io.github.deprec8.enigmadroid.ui.root.RootNavigationDisplay
import io.github.deprec8.enigmadroid.ui.theme.EnigmaDroidTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var devicesRepository: DevicesRepository

    @Inject
    lateinit var onboardingRepository: OnboardingRepository

    private var isOnboardingNeeded by mutableStateOf(false)
    private var isRemoteControlDeepLink by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        var isSetupFinished = false
        installSplashScreen().setKeepOnScreenCondition {
            !isSetupFinished
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        processIntent(intent)
        isSetupFinished = true
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
        isOnboardingNeeded = checkIsOnboardingNeeded()
        if (!isOnboardingNeeded) {
            val isDeepLink = isRemoteControlDeepLink(intent)
            isRemoteControlDeepLink = isDeepLink
            if (!isDeepLink) {
                handleDeviceIntent(intent)
            }
        } else {
            isRemoteControlDeepLink = false
        }
    }

    private fun isRemoteControlDeepLink(intent: Intent?): Boolean {
        intent ?: return false
        if (intent.action != Intent.ACTION_VIEW) return false
        return intent.data?.toString() == "enigmadroid://remotecontrol"
    }

    private fun checkIsOnboardingNeeded(): Boolean = runBlocking {
        return@runBlocking onboardingRepository.isOnboardingNeeded()
    }

    private fun handleDeviceIntent(intent: Intent?) = runBlocking {
        intent ?: return@runBlocking
        if (intent.action != "io.github.deprec8.enigmadroid.OPEN_WITH_DEVICE") return@runBlocking
        val deviceId = intent.getIntExtra("device_id", -1)
        if (deviceId != -1) {
            val currentDeviceId = devicesRepository.getCurrentDeviceId().first()
            if (deviceId != currentDeviceId) {
                devicesRepository.setCurrentDeviceId(deviceId)
            }
        }
    }
}