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
import io.github.deprec8.enigmadroid.data.repositories.DevicesRepository
import io.github.deprec8.enigmadroid.data.repositories.api.DeviceInfoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DeviceInfoViewModel(
    private val devicesRepository: DevicesRepository,
    private val deviceInfoRepository: DeviceInfoRepository
) : ViewModel() {

    val currentDevice = devicesRepository.current.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = null
    )

    val deviceInfo = currentDevice.map {
        deviceInfoRepository.observeDeviceInfo(it?.id ?: -1L).first()
    }.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = null
    )

    fun refresh() {
        viewModelScope.launch {
            currentDevice.first()?.let {
                deviceInfoRepository.refresh(it)
            }
        }
    }
}