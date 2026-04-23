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
import androidx.datastore.preferences.core.stringSetPreferencesKey
import io.github.deprec8.enigmadroid.data.objects.PreferenceKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private val tvSearchHistoryKey = stringSetPreferencesKey(PreferenceKey.TV_SEARCH_HISTORY)
    private val radioSearchHistoryKey = stringSetPreferencesKey(PreferenceKey.RADIO_SEARCH_HISTORY)
    private val tvEpgSearchHistoryKey = stringSetPreferencesKey(PreferenceKey.TV_EPG_SEARCH_HISTORY)
    private val radioEpgSearchHistoryKey =
        stringSetPreferencesKey(PreferenceKey.RADIO_EPG_SEARCH_HISTORY)
    private val moviesSearchHistoryKey =
        stringSetPreferencesKey(PreferenceKey.MOVIES_SEARCH_HISTORY)
    private val timersSearchHistoryKey =
        stringSetPreferencesKey(PreferenceKey.TIMERS_SEARCH_HISTORY)
    private val serviceEpgSearchHistoryKey =
        stringSetPreferencesKey(PreferenceKey.SERVICE_EPG_SEARCH_HISTORY)
    private val useSearchHistoryKey = booleanPreferencesKey(PreferenceKey.USE_SEARCH_HISTORY)

    fun getUseSearchHistory(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[useSearchHistoryKey] ?: true
    }

    suspend fun setUseSearchHistory(useSearchHistory: Boolean) {
        if (! useSearchHistory) {
            clearTvSearchHistory()
            clearRadioSearchHistory()
            clearTvEpgSearchHistory()
            clearRadioEpgSearchHistory()
            clearMoviesSearchHistory()
            clearTimersSearchHistory()
            clearServiceEpgSearchHistory()
        }
        dataStore.edit { preferences ->
            preferences[useSearchHistoryKey] = useSearchHistory
        }
    }

    private fun getHistory(key: Preferences.Key<Set<String>>): Flow<List<String>> =
        dataStore.data.map { preferences ->
            (preferences[key] ?: emptySet()).reversed()
        }

    private suspend fun addToHistory(key: Preferences.Key<Set<String>>, string: String) {
        if (getUseSearchHistory().first()) {
            dataStore.edit { preferences ->
                val strings = preferences[key] ?: emptySet()
                preferences[key] = strings + string
            }
        }
    }

    private suspend fun clearHistory(key: Preferences.Key<Set<String>>) {
        dataStore.edit { preferences ->
            preferences[key] = emptySet()
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