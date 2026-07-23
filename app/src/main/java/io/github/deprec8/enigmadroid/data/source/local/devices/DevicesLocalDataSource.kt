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
import androidx.datastore.preferences.core.longPreferencesKey
import io.github.deprec8.enigmadroid.common.constant.Keys
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class DevicesLocalDataSource(
    private val devicesDatabase: DevicesDatabase, private val dataStore: DataStore<Preferences>
) {
    private val currentDeviceIdKey = longPreferencesKey(Keys.CURRENT_DEVICE_ID)

    suspend fun setCurrentId(id: Long) {
        dataStore.edit { preferences ->
            preferences[currentDeviceIdKey] = id
        }
    }

    fun getCurrent(): Flow<Device?> {
        return getCurrentId().flatMapLatest { id ->
            devicesDatabase.devicesDao().get(id)
        }
    }

    suspend fun getCurrentStatic(): Device? {
        return devicesDatabase.devicesDao().getStatic(getCurrentIdStatic())
    }

    fun getCurrentId(): Flow<Long> {
        return dataStore.data.map { preferences ->
            preferences[currentDeviceIdKey] ?: -1L
        }
    }

    suspend fun getCurrentIdStatic(): Long {
        return dataStore.data.map { preferences ->
            preferences[currentDeviceIdKey] ?: -1L
        }.first()
    }

    fun getAll(): Flow<List<Device>> {
        return devicesDatabase.devicesDao().getAll()
    }

    suspend fun getCount(): Int {
        return devicesDatabase.devicesDao().getCount()
    }

    suspend fun add(device: Device): Boolean {
        val id = devicesDatabase.devicesDao().insert(device)

        if (getCurrentIdStatic() == -1L) {
            setCurrentId(id)
            return true
        }

        return false
    }

    suspend fun edit(oldDevice: Device, newDevice: Device): Boolean {
        devicesDatabase.devicesDao().update(newDevice.copy(id = oldDevice.id))

        return getCurrentIdStatic() == oldDevice.id
    }

    suspend fun delete(device: Device): Boolean {
        devicesDatabase.devicesDao().delete(device)

        if (getCurrentIdStatic() == device.id) {
            dataStore.edit { preferences ->
                preferences[currentDeviceIdKey] =
                    devicesDatabase.devicesDao().getPreviousOrNextId(device.id) ?: -1L
            }
            return true
        }

        return false
    }
}