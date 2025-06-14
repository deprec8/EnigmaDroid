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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.data.DevicesRepository
import io.github.deprec8.enigmadroid.data.OnboardingRepository
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    private val devicesRepository: DevicesRepository
) : ViewModel() {

    var name by mutableStateOf("")
        private set

    var ip by mutableStateOf("")
        private set

    var port by mutableStateOf("80")
        private set

    var livePort by mutableStateOf("8001")
        private set

    var isHttps by mutableStateOf(false)
        private set

    var isLogin by mutableStateOf(false)
        private set

    var user by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible.asStateFlow()

    fun togglePasswordVisibility() {
        _passwordVisible.value = ! _passwordVisible.value
    }

    fun updateName(newName: String) {
        name = newName
    }

    fun updateIp(newIp: String) {
        ip = newIp
    }

    fun updatePort(newPort: String) {
        port = newPort
    }

    fun updateLivePort(newLivePort: String) {
        livePort = newLivePort
    }

    fun toggleHttps() {
        isHttps = ! isHttps
        if (port == "80" && isHttps) {
            port = "443"
        } else if (port == "443" && ! isHttps) {
            port = "80"
        }
    }

    fun toggleLogin() {
        isLogin = ! isLogin
    }

    fun updateUser(newUser: String) {
        user = newUser
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }

    fun addDevice() {
        viewModelScope.launch {
            devicesRepository.addDevice(
                Device(
                    0,
                    name,
                    ip,
                    isHttps,
                    isLogin,
                    user,
                    password,
                    port,
                    livePort
                )
            )
        }
    }

    fun isEveryFieldFilled(): Boolean {
        return if (name != "" && ip != "" && port != "" && livePort != "") {
            if (isLogin) {
                user != "" && password != ""
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