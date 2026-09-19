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
import androidx.datastore.preferences.core.Preferences.Key
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

internal enum class LastFetchCategory(val key: Key<Long>, val multiplier: Float = 1.0f) {
    TIMERS(longPreferencesKey("last_fetched_timers"), 0.5f),
    MOVIES(longPreferencesKey("last_fetched_movies"), 2.0f),
    BOUQUETS_SERVICES(longPreferencesKey("last_fetched_bouquets_services"), 1.0f),
    PROVIDERS(longPreferencesKey("last_fetched_providers"), 5.0f),
    SATELLITES(longPreferencesKey("last_fetched_satellites"), 5.0f),
    PROVIDERS_SERVICES(longPreferencesKey("last_fetched_providers_services"), 1.0f),
    SATELLITES_SERVICES(longPreferencesKey("last_fetched_sat_services"), 1.0f),
    DEVICE_INFO(longPreferencesKey("last_fetched_device_info"), 10.0f)
}

internal class LastFetchLocalDataSource(
    private val dataStore: DataStore<Preferences>
) {
    private val masterIntervalKey = intPreferencesKey("master_refresh_interval_minutes")
    private val defaultMasterIntervalMinutes = 30

    fun needsRefresh(category: LastFetchCategory): Flow<Boolean> =
        combine(
            dataStore.data.map { it[category.key] ?: 0L },
            dataStore.data.map { it[masterIntervalKey] ?: defaultMasterIntervalMinutes }
        ) { lastFetch, masterInterval ->
            val intervalMs = TimeUnit.MINUTES.toMillis(
                (masterInterval * category.multiplier).toLong()
            )
            val currentTime = System.currentTimeMillis()
            (currentTime - lastFetch) > intervalMs
        }

    suspend fun updateLastFetchTimestamp(category: LastFetchCategory, timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[category.key] = timestamp
        }
    }
}