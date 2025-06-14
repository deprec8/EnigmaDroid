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
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import io.github.deprec8.enigmadroid.data.objects.PreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OnboardingRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private val onboardingKey = booleanPreferencesKey(PreferencesKeys.ONBOARDING_NEEDED)

    val isOnboardingNeeded: Flow<Boolean> = dataStore.data.map { preferences ->
        if (preferences.contains(onboardingKey)) {
            preferences[onboardingKey] != false
        } else {
            dataStore.edit { settings ->
                settings[onboardingKey] = true
            }
            true
        }
    }

    suspend fun completeOnboarding() {
        dataStore.edit { settings ->
            settings[onboardingKey] = false
        }
    }
}