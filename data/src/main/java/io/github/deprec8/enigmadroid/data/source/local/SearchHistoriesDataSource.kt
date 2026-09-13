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
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import io.github.deprec8.enigmadroid.core.database.common.PreferenceKeys
import io.github.deprec8.enigmadroid.core.database.model.SearchHistoryItemEntity
import io.github.deprec8.enigmadroid.core.database.room.AppDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SearchHistoriesDataSource(
    private val appDatabase: AppDatabase, private val dataStore: DataStore<Preferences>
) {
    private val useHistoriesKey = booleanPreferencesKey(PreferenceKeys.USE_SEARCH_HISTORIES)

    val useHistories = dataStore.data.map { preferences ->
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getHistory(type: ContentType) = appDatabase.searchHistoriesDao().get(type)


    fun getTypesWithHistory() =
        appDatabase.searchHistoriesDao().getTypesWithItems().map { it.toSet() }

    suspend fun addToHistory(type: ContentType, query: String) {
        if (useHistories.first()) {
            appDatabase.searchHistoriesDao().insertAndTrim(
                SearchHistoryItemEntity(
                    type = type, query = query, timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun clearHistories(types: Collection<ContentType>) {
        appDatabase.searchHistoriesDao().clear(types)
    }

    suspend fun clearHistories() {
        appDatabase.searchHistoriesDao().clearAll()
    }

    suspend fun deleteFromHistory(item: SearchHistoryItemEntity) {
        appDatabase.searchHistoriesDao().delete(item)
    }
}