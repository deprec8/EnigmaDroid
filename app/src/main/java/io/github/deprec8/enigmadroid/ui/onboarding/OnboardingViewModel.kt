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

package io.github.deprec8.enigmadroid.ui.onboarding

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.data.DevicesRepository
import io.github.deprec8.enigmadroid.data.OnboardingRepository
import io.github.deprec8.enigmadroid.data.objects.DefaultPort
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    private val devicesRepository: DevicesRepository
) : ViewModel() {

    val nameState = TextFieldState("")

    val ipState = TextFieldState("")

    val portState = TextFieldState(DefaultPort.HTTP)

    val livePortState = TextFieldState(DefaultPort.LIVE)

    var isHttps by mutableStateOf(false)
        private set

    var isLogin by mutableStateOf(false)
        private set

    val userState = TextFieldState("")

    val passwordState = TextFieldState("")

    private val _isEveryFieldFilled = MutableStateFlow(false)
    val isEveryFieldFilled = _isEveryFieldFilled.asStateFlow()

    init {
        viewModelScope.launch {
            val baseFlow = combine(
                snapshotFlow { nameState.text },
                snapshotFlow { ipState.text },
                snapshotFlow { portState.text },
                snapshotFlow { livePortState.text }) { name, ip, port, livePort ->
                listOf(name, ip, port, livePort).all { it.isNotBlank() }
            }

            val loginFlow = combine(
                snapshotFlow { userState.text },
                snapshotFlow { passwordState.text },
                snapshotFlow { isLogin }) { user, password, isLogin ->
                ! isLogin || (user.isNotBlank() && password.isNotBlank())
            }

            combine(baseFlow, loginFlow) { baseFilled, loginFilled ->
                baseFilled && loginFilled
            }.collectLatest {
                _isEveryFieldFilled.value = it
            }
        }
    }

    fun toggleHttps() {
        isHttps = ! isHttps
        if (portState.text == DefaultPort.HTTP && isHttps) {
            portState.setTextAndPlaceCursorAtEnd(DefaultPort.HTTPS)
        } else if (portState.text == DefaultPort.HTTPS && ! isHttps) {
            portState.setTextAndPlaceCursorAtEnd(DefaultPort.HTTP)

        }
    }

    fun toggleLogin() {
        isLogin = ! isLogin
    }

    fun completeOnboardingWithDevice() {
        viewModelScope.launch {
            if (_isEveryFieldFilled.value) {
                devicesRepository.addDevice(
                    Device(
                        0,
                        nameState.text.toString(),
                        ipState.text.toString().trim(),
                        isHttps,
                        isLogin,
                        userState.text.toString(),
                        passwordState.text.toString(),
                        portState.text.toString().trim(),
                        livePortState.text.toString().trim()
                    )
                )
                onboardingRepository.completeOnboarding()
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            onboardingRepository.completeOnboarding()
        }
    }
}