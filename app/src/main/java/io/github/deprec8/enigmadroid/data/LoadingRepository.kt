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
import io.github.deprec8.enigmadroid.data.source.local.devices.DevicesDatabase
import io.github.deprec8.enigmadroid.data.source.network.NetworkDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadingRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val devicesDatabase: DevicesDatabase,
    private val networkDataSource: NetworkDataSource
) {

    private val loadingStateKey = intPreferencesKey(PreferencesKeys.LOADING_STATE)

    init {
        CoroutineScope(Dispatchers.Default).launch {
            dataStore.edit { preferences ->
                preferences[loadingStateKey] = LoadingState.LOADING.id
            }
        }
    }

    fun getLoadingState(): Flow<LoadingState> {
        return dataStore.data.map { preferences ->
            LoadingState.entries[preferences[loadingStateKey] ?: 3]
        }
    }

    suspend fun updateLoadingState(forceUpdate: Boolean) {
        val currentLoadingState = dataStore.data.map { preferences ->
            LoadingState.entries[preferences[loadingStateKey] ?: 3]
        }.first()

        if (currentLoadingState == LoadingState.LOADING || forceUpdate) {
            if (currentLoadingState != LoadingState.LOADING) {
                dataStore.edit { preferences ->
                    preferences[loadingStateKey] = LoadingState.LOADING.id
                }
            }

            if (devicesDatabase.deviceDao().getAll().firstOrNull().isNullOrEmpty().not()) {
                if (networkDataSource.isNetworkAvailable()) {
                    if (networkDataSource.isDeviceOnline()) {
                        dataStore.edit { preferences ->
                            preferences[loadingStateKey] = LoadingState.LOADED.id
                        }
                    }
                } else {
                    dataStore.edit { preferences ->
                        preferences[loadingStateKey] = LoadingState.NO_NETWORK_AVAILABLE.id
                    }
                }
            } else {
                dataStore.edit { preferences ->
                    preferences[loadingStateKey] = LoadingState.NO_DEVICE_AVAILABLE.id
                }
            }
        }
    }
}