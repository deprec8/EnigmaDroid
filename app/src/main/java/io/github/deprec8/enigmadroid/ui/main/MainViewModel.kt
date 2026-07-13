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

package io.github.deprec8.enigmadroid.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.deprec8.enigmadroid.data.ConnectionState
import io.github.deprec8.enigmadroid.data.repositories.ApiRepository
import io.github.deprec8.enigmadroid.data.repositories.ConnectionRepository
import io.github.deprec8.enigmadroid.data.repositories.DevicesRepository
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel(
    private val devicesRepository: DevicesRepository,
    private val connectionRepository: ConnectionRepository,
    private val apiRepository: ApiRepository
) : ViewModel() {

    private val _currentDevice = MutableStateFlow<Device?>(null)
    val currentDevice: StateFlow<Device?> = _currentDevice.asStateFlow()

    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices = _devices.asStateFlow()

    private val _connectionState = MutableStateFlow(ConnectionState.CONNECTING)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    init {
        viewModelScope.launch {
            devicesRepository.getCurrentDevice().collectLatest { currentDevice ->
                _currentDevice.value = currentDevice
            }
        }
        viewModelScope.launch {
            connectionRepository.getLoadingState().collectLatest { state ->
                _connectionState.value = state
            }
        }
        viewModelScope.launch {
            devicesRepository.getAllDevices().collectLatest { devices ->
                _devices.value = devices
            }
        }
    }

    suspend fun updateLoadingState(forceUpdate: Boolean) {
        connectionRepository.checkConnection(forceUpdate)
    }

    suspend fun buildOwifUrl(): String {
        return apiRepository.buildOwifUrl()
    }

    fun setCurrentDevice(device: Device) {
        viewModelScope.launch {
            devicesRepository.setCurrentDevice(device)
        }
    }
}