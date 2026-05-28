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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import io.github.deprec8.enigmadroid.common.constant.PreferenceKeys
import io.github.deprec8.enigmadroid.common.enums.LoadingState
import io.github.deprec8.enigmadroid.data.source.local.devices.Device
import io.github.deprec8.enigmadroid.data.source.local.devices.DevicesLocalDataSource
import io.github.deprec8.enigmadroid.data.source.network.NetworkDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class DevicesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val networkDataSource: NetworkDataSource,
    private val devicesLocalDataSource: DevicesLocalDataSource
) {
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
        devicesLocalDataSource.setCurrentDevice(device)
        updateLoadingState()
    }

    suspend fun setCurrentDeviceId(id: Int) {
        devicesLocalDataSource.setCurrentDeviceId(id)
        updateLoadingState()
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

    suspend fun deleteDevice(device: Device) = withContext(NonCancellable) {
        devicesLocalDataSource.deleteDevice(device)

        if (devicesLocalDataSource.getAllDevicesStatic().isEmpty()) {
            dataStore.edit { preferences ->
                preferences[loadingStateKey] = LoadingState.NO_DEVICE_AVAILABLE.id
            }
        }
    }

    suspend fun editDevice(oldDevice: Device, newDevice: Device) = withContext(NonCancellable) {
        devicesLocalDataSource.editDevice(oldDevice, newDevice)
        if (devicesLocalDataSource.getCurrentDeviceIdStatic() == oldDevice.id) {
            updateLoadingState()
        }
    }

    suspend fun addDevice(device: Device) = withContext(NonCancellable) {
        devicesLocalDataSource.addDevice(device)
        if (devicesLocalDataSource.getAllDevicesStatic().size == 1) {
            updateLoadingState()
        }
    }
}