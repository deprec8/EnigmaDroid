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
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.data.objects.PreferenceKey
import io.github.deprec8.enigmadroid.data.source.local.devices.DeviceDatabase
import io.github.deprec8.enigmadroid.data.source.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val deviceDatabase: DeviceDatabase,
    private val networkDataSource: NetworkDataSource
) {

    private val onboardingKey = booleanPreferencesKey(PreferenceKey.ONBOARDING_NEEDED)
    private val loadingStateKey = intPreferencesKey(PreferenceKey.LOADING_STATE)

    suspend fun getOnboardingNeeded(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            if (preferences.contains(onboardingKey)) {
                preferences[onboardingKey] != false
            } else {
                dataStore.edit { settings ->
                    settings[onboardingKey] = true
                }
                true
            }
        }
    }

    suspend fun completeOnboarding() {
        val currentLoadingState = dataStore.data.map { preferences ->
            LoadingState.entries[preferences[loadingStateKey] ?: 3]
        }.first()

        if (currentLoadingState != LoadingState.LOADING) {
            dataStore.edit { preferences ->
                preferences[loadingStateKey] = LoadingState.LOADING.id
            }
        }

        if (deviceDatabase.deviceDao().getAll().firstOrNull().isNullOrEmpty().not()) {
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
        dataStore.edit { settings ->
            settings[onboardingKey] = false
        }
    }
}