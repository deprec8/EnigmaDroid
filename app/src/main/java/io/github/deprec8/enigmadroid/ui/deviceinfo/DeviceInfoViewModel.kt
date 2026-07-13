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

package io.github.deprec8.enigmadroid.ui.deviceinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.deprec8.enigmadroid.data.ConnectionState
import io.github.deprec8.enigmadroid.data.repositories.ApiRepository
import io.github.deprec8.enigmadroid.data.repositories.ConnectionRepository
import io.github.deprec8.enigmadroid.data.repositories.DevicesRepository
import io.github.deprec8.enigmadroid.model.api.DeviceInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DeviceInfoViewModel(
    private val apiRepository: ApiRepository,
    private val connectionRepository: ConnectionRepository,
    private val devicesRepository: DevicesRepository
) : ViewModel() {

    private val _deviceInfo = MutableStateFlow<DeviceInfo?>(null)
    val deviceInfo: StateFlow<DeviceInfo?> = _deviceInfo.asStateFlow()

    private val _connectionState = MutableStateFlow(ConnectionState.CONNECTING)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var fetchJob: Job? = null

    private var loadedDeviceId: Int? = null

    init {
        viewModelScope.launch {
            connectionRepository.getConnectionState().collectLatest { state ->
                _connectionState.value = state
            }
        }
    }

    fun checkConnection(forced: Boolean) {
        viewModelScope.launch {
            connectionRepository.checkConnection(forced)
        }
    }

    fun fetchData(isForced: Boolean = false) {
        viewModelScope.launch {
            val currentDeviceId = devicesRepository.getCurrentDeviceId().first()
            if (currentDeviceId != loadedDeviceId || isForced) {
                _deviceInfo.value = null
                loadedDeviceId = currentDeviceId
            }

            if (_deviceInfo.value == null) {
                fetchJob?.cancel()
                fetchJob = launch {
                    _deviceInfo.value = apiRepository.fetchDeviceInfo()
                }
            }
        }
    }
}