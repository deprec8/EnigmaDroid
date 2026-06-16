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

package io.github.deprec8.enigmadroid.data.source.local.devices

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import io.github.deprec8.enigmadroid.common.constant.PreferenceKeys
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class DevicesLocalDataSource(
    private val deviceDatabase: DeviceDatabase, private val dataStore: DataStore<Preferences>
) {

    private val currentDeviceKey = intPreferencesKey(PreferenceKeys.CURRENT_DEVICE)

    suspend fun setCurrentDevice(device: Device) = withContext(NonCancellable) {
        dataStore.edit { preferences ->
            preferences[currentDeviceKey] = device.id
        }
    }

    suspend fun setCurrentDeviceId(id: Int) = withContext(NonCancellable) {
        dataStore.edit { preferences ->
            preferences[currentDeviceKey] = id
        }
    }

    fun getCurrentDeviceFlow(): Flow<Device?> {
        return getCurrentDeviceIdFlow().flatMapLatest { id ->
            deviceDatabase.deviceDao().get(id)
        }
    }

    suspend fun getCurrentDeviceStatic(): Device? {
        return deviceDatabase.deviceDao().get(getCurrentDeviceIdStatic()).firstOrNull()
    }

    fun getCurrentDeviceIdFlow(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[currentDeviceKey] ?: -1
        }
    }

    suspend fun getCurrentDeviceIdStatic(): Int {
        return dataStore.data.map { preferences ->
            preferences[currentDeviceKey] ?: -1
        }.first()
    }

    fun getAllDevicesFlow(): Flow<List<Device>> {
        return deviceDatabase.deviceDao().getAll()
    }

    suspend fun getAllDevicesStatic(): List<Device> {
        return deviceDatabase.deviceDao().getAll().firstOrNull() ?: emptyList()
    }

    suspend fun deleteDevice(device: Device) = withContext(NonCancellable) {
        deviceDatabase.deviceDao().delete(device)

        if (getCurrentDeviceIdStatic() == device.id) {
            dataStore.edit { preferences ->
                preferences[currentDeviceKey] = getAllDevicesStatic().firstOrNull()?.id ?: -1
            }
        }
    }

    suspend fun editDevice(oldDevice: Device, newDevice: Device) = withContext(NonCancellable) {
        deviceDatabase.deviceDao().update(newDevice.copy(id = oldDevice.id))
    }

    suspend fun addDevice(device: Device) = withContext(NonCancellable) {
        deviceDatabase.deviceDao().insert(device)

        if (getAllDevicesStatic().size == 1) {
            setCurrentDevice(device)
        }
    }
}