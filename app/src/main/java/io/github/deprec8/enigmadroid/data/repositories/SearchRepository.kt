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
import io.github.deprec8.enigmadroid.common.constant.PreferenceKeys
import io.github.deprec8.enigmadroid.common.enums.ContentType
import io.github.deprec8.enigmadroid.data.source.local.SearchHistoriesDatabase
import io.github.deprec8.enigmadroid.data.source.local.SearchHistoryItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class SearchRepository(
    private val searchHistoriesDatabase: SearchHistoriesDatabase,
    private val dataStore: DataStore<Preferences>
) {

    private val useHistoriesKey = booleanPreferencesKey(PreferenceKeys.USE_SEARCH_HISTORIES)
    private val useHighlightingKey = booleanPreferencesKey(PreferenceKeys.USE_SEARCH_HIGHLIGHTING)

    fun getUseHistories() = dataStore.data.map { preferences ->
        preferences[useHistoriesKey] ?: true
    }

    suspend fun setUseHistories(value: Boolean) {
        if (!value) {
            clearHistories()
        }
        dataStore.edit { preferences ->
            preferences[useHistoriesKey] = value
        }
    }

    fun getUseHighlighting() = dataStore.data.map { preferences ->
        preferences[useHighlightingKey] ?: true
    }

    suspend fun setUseHighlighting(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[useHighlightingKey] = value
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getHistory(type: ContentType) = searchHistoriesDatabase.searchHistoriesDao().get(type)


    fun getTypesWithHistory() = searchHistoriesDatabase.searchHistoriesDao().getTypesWithItems()

    suspend fun addToHistory(type: ContentType, query: String) {
        if (getUseHistories().first()) {
            searchHistoriesDatabase.searchHistoriesDao().insertAndTrim(
                SearchHistoryItem(
                    type = type, query = query, timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun clearHistory(type: ContentType) {
        searchHistoriesDatabase.searchHistoriesDao().clear(type)
    }

    suspend fun clearHistories() {
        searchHistoriesDatabase.searchHistoriesDao().clearAll()
    }

    suspend fun deleteFromHistory(item: SearchHistoryItem) {
        searchHistoriesDatabase.searchHistoriesDao().delete(item)
    }
}