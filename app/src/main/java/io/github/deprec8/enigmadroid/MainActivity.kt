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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.github.deprec8.enigmadroid.data.DevicesRepository
import io.github.deprec8.enigmadroid.ui.main.MainPage
import io.github.deprec8.enigmadroid.ui.theme.EnigmaDroidTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var devicesRepository: DevicesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        handleIntent()
        setContent {
            EnigmaDroidTheme {
                MainPage()
            }
        }
    }

    fun handleIntent() {
        lifecycleScope.launch {
            intent?.let {
                when (it.action) {
                    "io.github.deprec8.enigmadroid.OPEN_WITH_DEVICE" -> {
                        val deviceId = it.getIntExtra("device_id", - 1)
                        if (deviceId != - 1 && deviceId != devicesRepository.getCurrentDeviceId()
                                .first()
                        ) {
                            devicesRepository.setCurrentDeviceId(deviceId)
                        }
                    }
                }
            }
        }
    }
}