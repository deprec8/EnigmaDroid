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

package io.github.deprec8.enigmadroid.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import io.github.deprec8.enigmadroid.core.database.common.PreferenceKeys
import io.github.deprec8.enigmadroid.core.database.model.DeviceEntity
import io.github.deprec8.enigmadroid.core.database.room.AppDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class DevicesDataSource(
    private val appDatabase: AppDatabase, private val dataStore: DataStore<Preferences>
) {
    private val currentDeviceIdKey = longPreferencesKey(PreferenceKeys.CURRENT_DEVICE_ID)

    val currentId = dataStore.data.map { preferences ->
        preferences[currentDeviceIdKey] ?: -1L
    }

    val current = currentId.flatMapLatest { id ->
        appDatabase.devicesDao().get(id)
    }

    suspend fun setCurrentId(id: Long) {
        dataStore.edit { preferences ->
            preferences[currentDeviceIdKey] = id
        }
    }

    suspend fun getCurrentStatic(): DeviceEntity? {
        return appDatabase.devicesDao().getStatic(currentId.first())
    }

    fun getAll(): Flow<List<DeviceEntity>> {
        return appDatabase.devicesDao().getAll()
    }

    suspend fun getCount(): Int {
        return appDatabase.devicesDao().getCount()
    }

    suspend fun add(deviceEntity: DeviceEntity): Boolean {
        val id = appDatabase.devicesDao().insert(deviceEntity)

        if (currentId.first() == -1L) {
            setCurrentId(id)
            return true
        }

        return false
    }

    suspend fun edit(oldDeviceEntity: DeviceEntity, newDeviceEntity: DeviceEntity): Boolean {
        appDatabase.devicesDao().update(newDeviceEntity.copy(id = oldDeviceEntity.id))

        return currentId.first() == oldDeviceEntity.id
    }

    suspend fun delete(deviceEntity: DeviceEntity): Boolean {
        appDatabase.devicesDao().delete(deviceEntity)

        if (currentId.first() == deviceEntity.id) {
            dataStore.edit { preferences ->
                preferences[currentDeviceIdKey] =
                    appDatabase.devicesDao().getPreviousOrNextId(deviceEntity.id) ?: -1L
            }
            return true
        }

        return false
    }
}