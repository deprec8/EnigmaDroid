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

package io.github.deprec8.enigmadroid.ui.epg

import androidx.lifecycle.viewModelScope
import io.github.deprec8.enigmadroid.common.enums.ContentType
import io.github.deprec8.enigmadroid.data.repositories.ApiRepository
import io.github.deprec8.enigmadroid.data.repositories.SearchHistoryRepository
import io.github.deprec8.enigmadroid.model.api.Bouquet
import io.github.deprec8.enigmadroid.model.api.Event
import io.github.deprec8.enigmadroid.model.api.EventBatchSet
import io.github.deprec8.enigmadroid.model.api.search
import io.github.deprec8.enigmadroid.ui.components.viewmodels.SearchableContentViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam

class EpgViewModel(
    @InjectedParam private val contentType: ContentType,
    private val apiRepository: ApiRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchableContentViewModel() {

    private val _eventBatchSet = MutableStateFlow<EventBatchSet?>(null)
    val eventBatchSet: StateFlow<EventBatchSet?> = _eventBatchSet.asStateFlow()

    private val _currentBouquetReference = MutableStateFlow("")
    val currentBouquetReference: StateFlow<String> = _currentBouquetReference.asStateFlow()

    private val _bouquets = MutableStateFlow<List<Bouquet>?>(null)
    val bouquets: StateFlow<List<Bouquet>?> = _bouquets.asStateFlow()

    private var fetchedEventBatchSetMap = emptyMap<String, EventBatchSet>()

    val filteredEvents = combine(_eventBatchSet, searchInput) { eventBatchSet, searchInput ->
        eventBatchSet?.eventBatches?.flatMap { it.events }?.search(searchInput)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    override val searchHistory = if (contentType == ContentType.Tv) {
        searchHistoryRepository.getTvEpgSearchHistory()
    } else {
        searchHistoryRepository.getRadioEpgSearchHistory()
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private suspend fun fetchBouquets() {
        val bouquets = apiRepository.fetchBouquets(contentType)
        _bouquets.value = bouquets
        val firstBRef = bouquets.firstOrNull()?.reference ?: ""
        if (_currentBouquetReference.value.isBlank() || bouquets.find { it.reference == _currentBouquetReference.value } == null) {
            _currentBouquetReference.value = firstBRef
        }
    }

    private suspend fun fetchEpgBatchSet() {
        val cached = fetchedEventBatchSetMap[_currentBouquetReference.value]
        if (cached != null) {
            _eventBatchSet.value = cached
            return
        }
        val epgBatchSet = apiRepository.fetchEpgEventBatchSet(_currentBouquetReference.value)
        _eventBatchSet.value = epgBatchSet
        fetchedEventBatchSetMap += _currentBouquetReference.value to epgBatchSet
    }

    fun addTimerForEvent(event: Event) {
        viewModelScope.launch {
            apiRepository.addTimerForEvent(
                event.serviceReference, event.id
            )
        }
    }

    fun setCurrentBouquet(bouquetReference: String) {
        fetchJob?.cancel()
        _currentBouquetReference.value = bouquetReference
        _eventBatchSet.value = null
        fetchJob = viewModelScope.launch {
            fetchEpgBatchSet()
        }
    }

    override fun onAddToSearchHistory(input: String) {
        viewModelScope.launch {
            if (contentType == ContentType.Tv) {
                searchHistoryRepository.addToTvEpgSearchHistory(input)
            } else {
                searchHistoryRepository.addToRadioEpgSearchHistory(input)
            }
        }
    }

    override fun onClearData() {
        _eventBatchSet.value = null
        _bouquets.value = null
        fetchedEventBatchSetMap = emptyMap()
    }

    override suspend fun onGetData() {
        fetchBouquets()
        fetchEpgBatchSet()
    }
}