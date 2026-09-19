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

import io.github.deprec8.enigmadroid.core.database.model.SearchHistoryItemEntity
import io.github.deprec8.enigmadroid.data.source.local.SearchHistoriesDataSource


class SearchRepository(
    private val searchHistoriesDataSource: SearchHistoriesDataSource,
) {

    val useHistories = searchHistoriesDataSource.useHistories

    suspend fun setUseHistories(value: Boolean) {
        searchHistoriesDataSource.setUseHistories(value)
    }

    fun getHistory(type: ContentType) = searchHistoriesDataSource.getHistory(type)

    fun getTypesWithHistory() = searchHistoriesDataSource.getTypesWithHistory()

    suspend fun addToHistory(type: ContentType, query: String) {
        searchHistoriesDataSource.addToHistory(type, query)
    }

    suspend fun clearHistories(types: Collection<ContentType>) {
        searchHistoriesDataSource.clearHistories(types)
    }

    suspend fun clearHistories() {
        searchHistoriesDataSource.clearHistories()
    }

    suspend fun deleteFromHistory(item: SearchHistoryItemEntity) {
        searchHistoriesDataSource.deleteFromHistory(item)
    }
}