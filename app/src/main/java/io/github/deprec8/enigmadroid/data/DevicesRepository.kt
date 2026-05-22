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

package io.github.deprec8.enigmadroid.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import io.github.deprec8.enigmadroid.common.constant.PreferenceKeys
import io.github.deprec8.enigmadroid.common.enums.LoadingState
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import io.github.deprec8.enigmadroid.data.source.local.devices.DeviceDatabase
import io.github.deprec8.enigmadroid.data.source.network.NetworkDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class DevicesRepository @Inject constructor(
    private val deviceDatabase: DeviceDatabase,
    private val dataStore: DataStore<Preferences>,
    private val networkDataSource: NetworkDataSource
) {

    private val currentDeviceKey = intPreferencesKey(PreferenceKeys.CURRENT_DEVICE)
    private val loadingStateKey = intPreferencesKey(PreferenceKeys.LOADING_STATE)

    private suspend fun updateLoadingState() {
        dataStore.edit { preferences ->
            preferences[loadingStateKey] = LoadingState.LOADING.id
        }

        if (networkDataSource.isDeviceOnline()) {
            dataStore.edit { preferences ->
                preferences[loadingStateKey] = LoadingState.LOADED.id
            }
        }
    }

    suspend fun setCurrentDevice(device: Device) {
        dataStore.edit { preferences ->
            preferences[currentDeviceKey] = device.id
        }
        updateLoadingState()
    }

    suspend fun setCurrentDeviceId(id: Int) {
        dataStore.edit { preferences ->
            preferences[currentDeviceKey] = id
        }
        updateLoadingState()
    }

    fun getCurrentDevice(): Flow<Device?> {
        return dataStore.data.map { preferences ->
            preferences[currentDeviceKey] ?: -1
        }.flatMapLatest { id ->
            deviceDatabase.deviceDao().get(id)
        }
    }

    fun getCurrentDeviceId(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[currentDeviceKey] ?: -1
        }
    }

    fun getAllDevices(): Flow<List<Device>> {
        return deviceDatabase.deviceDao().getAll()
    }

    suspend fun deleteDevice(device: Device) = withContext(NonCancellable) {
        val currentDeviceId = dataStore.data.map { preferences ->
            preferences[currentDeviceKey] ?: -1
        }.first()
        val allDevices = deviceDatabase.deviceDao().getAll().firstOrNull()

        deviceDatabase.deviceDao().delete(device)

        if (deviceDatabase.deviceDao().getAll().firstOrNull().isNullOrEmpty()) {
            dataStore.edit { preferences ->
                preferences[loadingStateKey] = LoadingState.NO_DEVICE_AVAILABLE.id
            }
        }

        if (currentDeviceId == device.id) {
            dataStore.edit { preferences ->
                preferences[currentDeviceKey] = allDevices?.first()?.id ?: -1
            }
        }
    }

    suspend fun editDevice(oldDevice: Device, newDevice: Device) = withContext(NonCancellable) {
        deviceDatabase.deviceDao().update(newDevice.copy(id = oldDevice.id))
        updateLoadingState()
    }

    suspend fun addDevice(device: Device) = withContext(NonCancellable) {
        deviceDatabase.deviceDao().insert(device)
        if (deviceDatabase.deviceDao().getAll().firstOrNull()?.size == 1) {
            updateLoadingState()
        }
    }
}