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

package io.github.deprec8.enigmadroid.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.data.objects.PreferencesKeys
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import io.github.deprec8.enigmadroid.data.source.local.devices.DevicesDatabase
import io.github.deprec8.enigmadroid.data.source.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DevicesRepository @Inject constructor(
    private val devicesDatabase: DevicesDatabase,
    private val dataStore: DataStore<Preferences>,
    private val networkDataSource: NetworkDataSource
) {

    private val currentDeviceKey = intPreferencesKey(PreferencesKeys.CURRENT_DEVICE)
    private val loadingStateKey = intPreferencesKey(PreferencesKeys.LOADING_STATE)

    fun getCurrentDeviceId(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[currentDeviceKey] ?: 0
        }
    }

    suspend fun setCurrentDeviceId(listId: Int) {
        dataStore.edit { preferences ->
            preferences[currentDeviceKey] = listId
            preferences[loadingStateKey] = LoadingState.LOADING.id
        }
        if (networkDataSource.isDeviceOnline()) {
            dataStore.edit { preferences ->
                preferences[loadingStateKey] = LoadingState.LOADED.id
            }
        } else {
            dataStore.edit { preferences ->
                preferences[loadingStateKey] = LoadingState.DEVICE_NOT_ONLINE.id
            }
        }
    }

    fun getCurrentDevice(): Flow<Device?> {
        val listId = dataStore.data.map { preferences ->
            preferences[currentDeviceKey]
        }
        val allDevices = devicesDatabase.deviceDao().getAll()
        return combine(listId, allDevices) { listIdF, allDevicesF ->
            allDevicesF.getOrNull(listIdF ?: 0)
        }
    }

    fun getAllDevices(): Flow<List<Device>> {
        return devicesDatabase.deviceDao().getAll()
    }

    suspend fun deleteDevice(deviceId: Int) {
        devicesDatabase.deviceDao().delete(deviceId)
        if (devicesDatabase.deviceDao().getAll().firstOrNull().isNullOrEmpty()) {
            dataStore.edit { preferences ->
                preferences[loadingStateKey] = LoadingState.NO_DEVICE_AVAILABLE.id
            }
        }
    }

    suspend fun editDevice(oldDevice: Device, newDevice: Device) {
        devicesDatabase.deviceDao().update(newDevice.copy(id = oldDevice.id))
        if (networkDataSource.isDeviceOnline()) {
            dataStore.edit { preferences ->
                preferences[loadingStateKey] = LoadingState.LOADED.id
            }
        } else {
            dataStore.edit { preferences ->
                preferences[loadingStateKey] = LoadingState.DEVICE_NOT_ONLINE.id
            }
        }
    }

    suspend fun addDevice(device: Device) {
        devicesDatabase.deviceDao().insert(device)
        if (devicesDatabase.deviceDao().getAll().firstOrNull() !!.size == 1) {
            if (networkDataSource.isDeviceOnline()) {
                dataStore.edit { preferences ->
                    preferences[loadingStateKey] = LoadingState.LOADED.id
                }
            } else {
                dataStore.edit { preferences ->
                    preferences[loadingStateKey] = LoadingState.DEVICE_NOT_ONLINE.id
                }
            }
        }
    }
}