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
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import io.github.deprec8.enigmadroid.common.constant.Keys
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class OnboardingRepository(
    private val dataStore: DataStore<Preferences>
) {
    private val onboardingNeededKey = booleanPreferencesKey(Keys.ONBOARDING_NEEDED)

    suspend fun getOnboardingNeeded() = dataStore.data.map { preferences ->
        preferences[onboardingNeededKey] ?: true
    }.first()

    suspend fun finishOnboarding() {
        dataStore.edit { preferences ->
            preferences[onboardingNeededKey] = false
        }
    }
}