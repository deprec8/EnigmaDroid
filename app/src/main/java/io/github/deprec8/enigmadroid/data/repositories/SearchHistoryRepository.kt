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
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.deprec8.enigmadroid.common.constant.PreferenceKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json


class SearchHistoryRepository(private val dataStore: DataStore<Preferences>) {

    private val tvSearchHistoryKey = stringPreferencesKey(PreferenceKeys.TV_SEARCH_HISTORY)
    private val radioSearchHistoryKey = stringPreferencesKey(PreferenceKeys.RADIO_SEARCH_HISTORY)
    private val tvEpgSearchHistoryKey =
        stringPreferencesKey(PreferenceKeys.TV_EPG_SEARCH_HISTORY)
    private val radioEpgSearchHistoryKey =
        stringPreferencesKey(PreferenceKeys.RADIO_EPG_SEARCH_HISTORY)
    private val moviesSearchHistoryKey =
        stringPreferencesKey(PreferenceKeys.MOVIES_SEARCH_HISTORY)
    private val timersSearchHistoryKey =
        stringPreferencesKey(PreferenceKeys.TIMERS_SEARCH_HISTORY)
    private val serviceEpgSearchHistoryKey =
        stringPreferencesKey(PreferenceKeys.SERVICE_EPG_SEARCH_HISTORY)
    private val useSearchHistoryKey = booleanPreferencesKey(PreferenceKeys.USE_SEARCH_HISTORY)

    fun getUseSearchHistory(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[useSearchHistoryKey] ?: true
    }

    suspend fun setUseSearchHistory(useSearchHistory: Boolean) {
        dataStore.edit { preferences ->
            if (!useSearchHistory) {
                preferences[tvSearchHistoryKey] = ""
                preferences[radioSearchHistoryKey] = ""
                preferences[tvEpgSearchHistoryKey] = ""
                preferences[radioEpgSearchHistoryKey] = ""
                preferences[moviesSearchHistoryKey] = ""
                preferences[timersSearchHistoryKey] = ""
                preferences[serviceEpgSearchHistoryKey] = ""
            }
            preferences[useSearchHistoryKey] = useSearchHistory
        }
    }

    private fun getHistory(key: Preferences.Key<String>): Flow<List<String>> =
        dataStore.data.map { preferences ->
            getHistoryList(preferences[key])
        }

    private suspend fun addToHistory(key: Preferences.Key<String>, string: String) {
        if (getUseSearchHistory().first()) {
            dataStore.edit { preferences ->
                val currentHistory = getHistoryList(preferences[key])
                val newHistory = (listOf(string) + currentHistory).distinct().take(50)
                preferences[key] = Json.encodeToString(newHistory)
            }
        }
    }

    private suspend fun clearHistory(key: Preferences.Key<String>) {
        dataStore.edit { preferences ->
            preferences[key] = ""
        }
    }

    private fun getHistoryList(jsonString: String?): List<String> {
        return try {
            if (jsonString.isNullOrBlank()) emptyList()
            else Json.decodeFromString(jsonString)
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun getTvSearchHistory() = getHistory(tvSearchHistoryKey)
    suspend fun addToTvSearchHistory(string: String) = addToHistory(tvSearchHistoryKey, string)
    suspend fun clearTvSearchHistory() = clearHistory(tvSearchHistoryKey)

    fun getTimersSearchHistory() = getHistory(timersSearchHistoryKey)
    suspend fun addToTimersSearchHistory(string: String) =
        addToHistory(timersSearchHistoryKey, string)

    suspend fun clearTimersSearchHistory() = clearHistory(timersSearchHistoryKey)

    fun getRadioSearchHistory() = getHistory(radioSearchHistoryKey)
    suspend fun addToRadioSearchHistory(string: String) =
        addToHistory(radioSearchHistoryKey, string)

    suspend fun clearRadioSearchHistory() = clearHistory(radioSearchHistoryKey)

    fun getTvEpgSearchHistory() = getHistory(tvEpgSearchHistoryKey)
    suspend fun addToTvEpgSearchHistory(string: String) =
        addToHistory(tvEpgSearchHistoryKey, string)

    suspend fun clearTvEpgSearchHistory() = clearHistory(tvEpgSearchHistoryKey)

    fun getRadioEpgSearchHistory() = getHistory(radioEpgSearchHistoryKey)
    suspend fun addToRadioEpgSearchHistory(string: String) =
        addToHistory(radioEpgSearchHistoryKey, string)

    suspend fun clearRadioEpgSearchHistory() = clearHistory(radioEpgSearchHistoryKey)

    fun getMoviesSearchHistory() = getHistory(moviesSearchHistoryKey)
    suspend fun addToMoviesSearchHistory(string: String) =
        addToHistory(moviesSearchHistoryKey, string)

    suspend fun clearMoviesSearchHistory() = clearHistory(moviesSearchHistoryKey)

    fun getServiceEpgSearchHistory() = getHistory(serviceEpgSearchHistoryKey)
    suspend fun addToServiceEpgSearchHistory(string: String) =
        addToHistory(serviceEpgSearchHistoryKey, string)

    suspend fun clearServiceEpgSearchHistory() = clearHistory(serviceEpgSearchHistoryKey)
}