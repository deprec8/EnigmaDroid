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

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.github.deprec8.enigmadroid.common.constant.Keys


class CurrentDeviceIdMigration1 : DataMigration<Preferences> {

    private val oldKey = intPreferencesKey("current_device")
    private val newKey = longPreferencesKey(Keys.CURRENT_DEVICE_ID)

    override suspend fun shouldMigrate(currentData: Preferences): Boolean =
        currentData.contains(oldKey)

    override suspend fun migrate(currentData: Preferences): Preferences =
        currentData.toMutablePreferences().apply {
            val oldValue = currentData[oldKey]
            if (oldValue != null) {
                this[newKey] = oldValue.toLong()
            }
            remove(oldKey)
        }.toPreferences()

    override suspend fun cleanUp() {}
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Keys.DATASTORE, produceMigrations = {
        listOf(
            CurrentDeviceIdMigration1()
        )
    })