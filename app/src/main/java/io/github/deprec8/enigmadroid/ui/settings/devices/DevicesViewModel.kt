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

package io.github.deprec8.enigmadroid.ui.settings.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.deprec8.enigmadroid.data.repositories.DevicesRepository
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DevicesViewModel(private val devicesRepository: DevicesRepository) : ViewModel() {

    val currentDeviceId = devicesRepository.getCurrentDeviceId().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), -1
    )

    val devices = devicesRepository.getAllDevices().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun buildDeviceOwifUrl(device: Device) = device.buildOwifUrl()

    fun setCurrentDevice(device: Device) {
        viewModelScope.launch {
            devicesRepository.setCurrentDevice(device)
        }
    }

    fun deleteDevice(device: Device) {
        viewModelScope.launch {
            devicesRepository.deleteDevice(device)
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
                oldDevice, newDevice
            )
        }
    }
}