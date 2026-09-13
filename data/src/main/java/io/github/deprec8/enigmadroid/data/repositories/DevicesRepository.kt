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

import io.github.deprec8.enigmadroid.core.database.model.DeviceEntity
import io.github.deprec8.enigmadroid.core.database.source.DevicesDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalCoroutinesApi::class)
class DevicesRepository(
    private val devicesDataSource: DevicesDataSource
) {

    val current = devicesDataSource.current
    val currentId = devicesDataSource.currentId

    suspend fun setCurrentDeviceId(id: Long) {
        devicesDataSource.setCurrentId(id)
    }

    fun getDevices(): Flow<List<DeviceEntity>> {
        return devicesDataSource.getAll()
    }

    suspend fun addDevice(device: DeviceEntity) {
        devicesDataSource.add(device)
    }

    suspend fun editDevice(oldDevice: DeviceEntity, newDevice: DeviceEntity) {
        devicesDataSource.edit(oldDevice, newDevice)
    }

    suspend fun deleteDevice(device: DeviceEntity) {
        devicesDataSource.delete(device)
    }
}