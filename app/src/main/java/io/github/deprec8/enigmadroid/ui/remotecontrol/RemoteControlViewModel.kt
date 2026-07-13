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

package io.github.deprec8.enigmadroid.ui.remotecontrol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.deprec8.enigmadroid.common.enums.RemoteControlKey
import io.github.deprec8.enigmadroid.common.enums.RemoteControlPowerKey
import io.github.deprec8.enigmadroid.data.ConnectionState
import io.github.deprec8.enigmadroid.data.repositories.ApiRepository
import io.github.deprec8.enigmadroid.data.repositories.ConnectionRepository
import io.github.deprec8.enigmadroid.data.repositories.DevicesRepository
import io.github.deprec8.enigmadroid.data.repositories.DownloadRepository
import io.github.deprec8.enigmadroid.data.repositories.SettingsRepository
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RemoteControlViewModel(
    private val apiRepository: ApiRepository,
    private val devicesRepository: DevicesRepository,
    private val connectionRepository: ConnectionRepository,
    private val downloadRepository: DownloadRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val connectionState: StateFlow<ConnectionState> =
        connectionRepository.getConnectionState().stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), ConnectionState.CONNECTING
        )

    private val _currentDevice = MutableStateFlow<Device?>(null)
    val currentDevice: StateFlow<Device?> = _currentDevice.asStateFlow()

    private val _remoteControlVibration = MutableStateFlow(false)
    val remoteControlVibration: StateFlow<Boolean> = _remoteControlVibration.asStateFlow()

    init {
        viewModelScope.launch {
            devicesRepository.getCurrentDevice().collectLatest { currentDevice ->
                _currentDevice.value = currentDevice
            }
        }
        viewModelScope.launch {
            settingsRepository.getRemoteControlVibration().collectLatest { value ->
                _remoteControlVibration.value = value
            }
        }
    }

    fun checkConnection(forced: Boolean) {
        viewModelScope.launch {
            connectionRepository.checkConnection(forced)
        }
    }

    fun fetchScreenshot() {
        viewModelScope.launch {
            downloadRepository.fetchScreenshot()
        }
    }

    fun onKeyClicked(key: RemoteControlKey) {
        viewModelScope.launch {
            apiRepository.remoteControlCall(key)
        }
    }

    fun onPowerKeyClicked(powerKey: RemoteControlPowerKey) {
        viewModelScope.launch {
            apiRepository.setPowerState(powerKey)
            connectionRepository.checkConnection(true)
        }
    }
}