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

package io.github.deprec8.enigmadroid.ui.remoteControl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.data.ApiRepository
import io.github.deprec8.enigmadroid.data.DevicesRepository
import io.github.deprec8.enigmadroid.data.DownloadRepository
import io.github.deprec8.enigmadroid.data.LoadingRepository
import io.github.deprec8.enigmadroid.data.SettingsRepository
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.data.enums.RemoteControlButtonType
import io.github.deprec8.enigmadroid.data.enums.RemoteControlPowerButtonType
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemoteControlViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val devicesRepository: DevicesRepository,
    private val loadingRepository: LoadingRepository,
    private val downloadRepository: DownloadRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _loadingState = MutableStateFlow(LoadingState.LOADING)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    private val _currentDevice = MutableStateFlow<Device?>(null)
    val currentDevice: StateFlow<Device?> = _currentDevice.asStateFlow()

    private val _remoteControlVibration = MutableStateFlow(false)
    val remoteVibration: StateFlow<Boolean> = _remoteControlVibration.asStateFlow()

    init {
        viewModelScope.launch {
            devicesRepository.getCurrentDevice().collectLatest { currentDevice ->
                _currentDevice.value = currentDevice
            }
        }
        viewModelScope.launch {
            loadingRepository.getLoadingState().collectLatest { state ->
                _loadingState.value = state
            }
        }
        viewModelScope.launch {
            settingsRepository.getRemoteControlVibration().collectLatest { value ->
                _remoteControlVibration.value = value
            }
        }
    }

    suspend fun updateLoadingState(isForcedUpdate: Boolean) {
        loadingRepository.updateLoadingState(isForcedUpdate)
    }

    fun fetchScreenshot() {
        viewModelScope.launch {
            downloadRepository.fetchScreenshot()
        }
    }

    fun onButtonClicked(type: RemoteControlButtonType) {
        viewModelScope.launch {
            apiRepository.remoteControlCall(type)
        }
    }

    fun onPowerButtonClicked(type: RemoteControlPowerButtonType) {
        viewModelScope.launch {
            apiRepository.setPowerState(type)
            updateLoadingState(true)
        }
    }
}