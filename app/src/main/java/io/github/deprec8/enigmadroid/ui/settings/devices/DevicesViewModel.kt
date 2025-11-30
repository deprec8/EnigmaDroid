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

package io.github.deprec8.enigmadroid.ui.settings.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.data.DevicesRepository
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(private val devicesRepository: DevicesRepository) :
    ViewModel() {

    private val _currentDeviceId = MutableStateFlow<Int?>(null)
    val currentDeviceId: StateFlow<Int?> = _currentDeviceId.asStateFlow()

    private val _allDevices = MutableStateFlow<List<Device>>(emptyList())
    val allDevices: StateFlow<List<Device>> = _allDevices.asStateFlow()

    init {
        viewModelScope.launch {
            devicesRepository.getAllDevices().collectLatest { allDevices ->
                _allDevices.value = allDevices
            }
        }
        viewModelScope.launch {
            devicesRepository.getCurrentDeviceId().collectLatest { currentDeviceId ->
                _currentDeviceId.value = currentDeviceId
            }
        }
    }

    fun makeDeviceOWIFURL(device: Device): String {
        var url = ""
        url += if (device.isHttps) "https://" else "http://"

        if (device.isLogin) {
            url += "${device.user}:${device.password}@"
        }
        url += "${device.ip}:${device.port}"
        return url
    }

    fun setCurrentDevice(listId: Int) {
        viewModelScope.launch {
            devicesRepository.setCurrentDeviceId(listId)
        }
    }

    fun deleteDevice(listId: Int) {
        viewModelScope.launch {
            devicesRepository.deleteDevice(_allDevices.value[listId].id)
            if (_currentDeviceId.value == listId) {
                devicesRepository.setCurrentDeviceId(0)
            } else _currentDeviceId.value?.let {
                if (it > listId) {
                    devicesRepository.setCurrentDeviceId(it - 1)
                }
            }
        }
    }

    fun addDevice(newDevice: Device) {
        viewModelScope.launch {
            devicesRepository.addDevice(
                newDevice
            )
        }
    }

    fun editDevice(oldDevice: Device, newDevice: Device) {
        viewModelScope.launch {
            devicesRepository.editDevice(
                oldDevice,
                newDevice
            )
        }
    }
}