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

package io.github.deprec8.enigmadroid.ui.onboarding

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.data.DevicesRepository
import io.github.deprec8.enigmadroid.data.OnboardingRepository
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    private val devicesRepository: DevicesRepository
) : ViewModel() {

    val nameState = TextFieldState("")

    val ipState = TextFieldState("")

    val portState = TextFieldState("80")

    val livePortState = TextFieldState("8001")

    var isHttps by mutableStateOf(false)
        private set

    var isLogin by mutableStateOf(false)
        private set

    val userState = TextFieldState("")

    val passwordState = TextFieldState("")

    fun toggleHttps() {
        isHttps = ! isHttps
        if (portState.text == "80" && isHttps) {
            portState.setTextAndPlaceCursorAtEnd("443")
        } else if (portState.text == "443" && ! isHttps) {
            portState.setTextAndPlaceCursorAtEnd("80")

        }
    }

    fun toggleLogin() {
        isLogin = ! isLogin
    }

    fun addDevice() {
        viewModelScope.launch {
            devicesRepository.addDevice(
                Device(
                    0,
                    nameState.text.toString(),
                    ipState.text.toString(),
                    isHttps,
                    isLogin,
                    userState.text.toString(),
                    passwordState.text.toString(),
                    portState.text.toString(),
                    livePortState.text.toString()
                )
            )
        }
    }

    fun isEveryFieldFilled(): Boolean {
        return if (nameState.text.isNotBlank() && ipState.text.isNotBlank() && portState.text.isNotBlank() && livePortState.text.isNotBlank()) {
            if (isLogin) {
                userState.text.isNotBlank() && passwordState.text.isNotBlank()
            } else {
                true
            }
        } else {
            false
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            onboardingRepository.completeOnboarding()
        }
    }
}