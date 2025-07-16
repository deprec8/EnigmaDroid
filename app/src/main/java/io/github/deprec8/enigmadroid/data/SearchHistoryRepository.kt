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
import androidx.datastore.preferences.core.stringSetPreferencesKey
import io.github.deprec8.enigmadroid.data.objects.PreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchHistoryRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private val tvSearchHistoryKey = stringSetPreferencesKey(PreferencesKeys.TV_SEARCH_HISTORY)
    private val radioSearchHistoryKey =
        stringSetPreferencesKey(PreferencesKeys.RADIO_SEARCH_HISTORY)
    private val epgSearchHistoryKey = stringSetPreferencesKey(PreferencesKeys.EPG_SEARCH_HISTORY)
    private val moviesSearchHistoryKey =
        stringSetPreferencesKey(PreferencesKeys.MOVIES_SEARCH_HISTORY)
    private val timersSearchHistoryKey =
        stringSetPreferencesKey(PreferencesKeys.TIMERS_SEARCH_HISTORY)


    fun getTVSearchHistory(): Flow<List<String>> = dataStore.data.map { preferences ->
        (preferences[tvSearchHistoryKey] ?: emptySet()).reversed()
    }

    suspend fun addToTVSearchHistory(string: String) {
        dataStore.edit { preferences ->
            preferences[tvSearchHistoryKey] = dataStore.data.map { preferences ->
                val temp = (preferences[tvSearchHistoryKey] ?: emptySet()).toMutableSet()
                temp.add(string)
                temp.toSet()
            }.first()
        }
    }

    suspend fun clearTVSearchHistory() {
        dataStore.edit { preferences ->
            preferences[tvSearchHistoryKey] = emptySet()
        }
    }

    fun getTimersSearchHistory(): Flow<List<String>> = dataStore.data.map { preferences ->
        (preferences[timersSearchHistoryKey] ?: emptySet()).reversed()
    }

    suspend fun addToTimersSearchHistory(string: String) {
        dataStore.edit { preferences ->
            preferences[timersSearchHistoryKey] = dataStore.data.map { preferences ->
                val temp = (preferences[timersSearchHistoryKey] ?: emptySet()).toMutableSet()
                temp.add(string)
                temp.toSet()
            }.first()
        }
    }

    suspend fun clearTimersSearchHistory() {
        dataStore.edit { preferences ->
            preferences[timersSearchHistoryKey] = emptySet()
        }
    }

    fun getRadioSearchHistory(): Flow<List<String>> = dataStore.data.map { preferences ->
        (preferences[radioSearchHistoryKey] ?: emptySet()).reversed()
    }

    suspend fun addToRadioSearchHistory(string: String) {
        dataStore.edit { preferences ->
            preferences[radioSearchHistoryKey] = dataStore.data.map { preferences ->
                val temp = (preferences[radioSearchHistoryKey] ?: emptySet()).toMutableSet()
                temp.add(string)
                temp.toSet()
            }.first()
        }
    }

    suspend fun clearRadioSearchHistory() {
        dataStore.edit { preferences ->
            preferences[radioSearchHistoryKey] = emptySet()
        }
    }

    fun getEPGSearchHistory(): Flow<List<String>> = dataStore.data.map { preferences ->
        (preferences[epgSearchHistoryKey] ?: emptySet()).reversed()
    }

    suspend fun addToEPGSearchHistory(string: String) {
        dataStore.edit { preferences ->
            preferences[epgSearchHistoryKey] = dataStore.data.map { preferences ->
                val temp = (preferences[epgSearchHistoryKey] ?: emptySet()).toMutableSet()
                temp.add(string)
                temp.toSet()
            }.first()
        }
    }

    suspend fun clearEPGSearchHistory() {
        dataStore.edit { preferences ->
            preferences[epgSearchHistoryKey] = emptySet()
        }
    }

    fun getMoviesSearchHistory(): Flow<List<String>> = dataStore.data.map { preferences ->
        (preferences[moviesSearchHistoryKey] ?: emptySet()).reversed()
    }

    suspend fun addToMoviesSearchHistory(string: String) {
        dataStore.edit { preferences ->
            preferences[moviesSearchHistoryKey] = dataStore.data.map { preferences ->
                val temp = (preferences[moviesSearchHistoryKey] ?: emptySet()).toMutableSet()
                temp.add(string)
                temp.toSet()
            }.first()
        }
    }

    suspend fun clearMoviesSearchHistory() {
        dataStore.edit { preferences ->
            preferences[moviesSearchHistoryKey] = emptySet()
        }
    }
}