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

package io.github.deprec8.enigmadroid.data.repositories

import io.github.deprec8.enigmadroid.data.ConnectionState
import io.github.deprec8.enigmadroid.data.ConnectionStateHolder
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import io.github.deprec8.enigmadroid.data.source.local.devices.DevicesLocalDataSource
import io.github.deprec8.enigmadroid.data.source.network.NetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class DevicesRepository(
    private val connectionStateHolder: ConnectionStateHolder,
    private val networkDataSource: NetworkDataSource,
    private val devicesLocalDataSource: DevicesLocalDataSource
) {
    private var checkJob: Job? = null

    suspend fun setCurrentDevice(device: Device) = withContext(Dispatchers.IO) {
        devicesLocalDataSource.setCurrentDevice(device)
        checkJob?.cancel()
        checkJob = launch { networkDataSource.checkConnection() }
    }

    suspend fun setCurrentDeviceId(id: Int) = withContext(Dispatchers.IO) {
        devicesLocalDataSource.setCurrentDeviceId(id)
        checkJob?.cancel()
        checkJob = launch { networkDataSource.checkConnection() }
    }

    fun getCurrentDevice(): Flow<Device?> {
        return devicesLocalDataSource.getCurrentDeviceFlow()
    }

    fun getCurrentDeviceId(): Flow<Int> {
        return devicesLocalDataSource.getCurrentDeviceIdFlow()
    }

    fun getAllDevices(): Flow<List<Device>> {
        return devicesLocalDataSource.getAllDevicesFlow()
    }

    suspend fun deleteDevice(device: Device) = withContext(Dispatchers.IO) {
        devicesLocalDataSource.deleteDevice(device)

        if (devicesLocalDataSource.getAllDevicesStatic().isEmpty()) {
            connectionStateHolder.updateConnectionState(ConnectionState.NO_DEVICE_AVAILABLE)
        }
    }

    suspend fun editDevice(oldDevice: Device, newDevice: Device) = withContext(Dispatchers.IO) {
        devicesLocalDataSource.editDevice(oldDevice, newDevice)
        if (devicesLocalDataSource.getCurrentDeviceIdStatic() == oldDevice.id) {
            checkJob?.cancel()
            checkJob = launch { networkDataSource.checkConnection() }
        }
    }

    suspend fun addDevice(device: Device) = withContext(Dispatchers.IO) {
        devicesLocalDataSource.addDevice(device)
        if (devicesLocalDataSource.getAllDevicesStatic().size == 1) {
            checkJob?.cancel()
            checkJob = launch { networkDataSource.checkConnection() }
        }
    }
}