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

package io.github.deprec8.enigmadroid.ui.epg.tvEpg

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.deprec8.enigmadroid.data.ApiRepository
import io.github.deprec8.enigmadroid.data.LoadingRepository
import io.github.deprec8.enigmadroid.data.SearchHistoryRepository
import io.github.deprec8.enigmadroid.data.SettingsRepository
import io.github.deprec8.enigmadroid.data.enums.ApiType
import io.github.deprec8.enigmadroid.data.enums.LoadingState
import io.github.deprec8.enigmadroid.model.api.events.Event
import io.github.deprec8.enigmadroid.model.api.events.EventBatchSet
import io.github.deprec8.enigmadroid.utils.FilterUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvEpgViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val loadingRepository: LoadingRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _epgBatchSet = MutableStateFlow(EventBatchSet())
    val eventBatchSet: StateFlow<EventBatchSet> = _epgBatchSet.asStateFlow()

    private val _filteredEvents = MutableStateFlow<List<Event>?>(null)
    val filteredEvents: StateFlow<List<Event>?> = _filteredEvents.asStateFlow()

    private val _loadingState = MutableStateFlow(LoadingState.LOADING)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    private val _searchInput = MutableStateFlow("")
    val searchInput: StateFlow<String> = _searchInput.asStateFlow()

    private val _useSearchHighlighting = MutableStateFlow(true)
    val useSearchHighlighting: StateFlow<Boolean> = _useSearchHighlighting.asStateFlow()

    private val _currentBouquet = MutableStateFlow("")
    val currentBouquet: StateFlow<String> = _currentBouquet.asStateFlow()

    private val _bouquets = MutableStateFlow<List<List<String>>>(emptyList())
    val bouquets: StateFlow<List<List<String>>> = _bouquets.asStateFlow()

    val searchFieldState = TextFieldState()

    private var fetchJob: Job? = null

    init {
        viewModelScope.launch {
            loadingRepository.getLoadingState().collectLatest { state ->
                _loadingState.value = state
            }
        }
        viewModelScope.launch {
            combine(_epgBatchSet, _searchInput) { eventBatchSet, searchInput ->
                if (searchInput.isNotBlank() && eventBatchSet.eventBatches.isNotEmpty()) {
                    searchHistoryRepository.addToTvEpgSearchHistory(searchInput)
                    FilterUtils.filterEvents(
                        searchInput, eventBatchSet.eventBatches.flatMap { it.events })
                } else {
                    null
                }
            }.collectLatest {
                _filteredEvents.value = it
            }
        }
        viewModelScope.launch {
            searchHistoryRepository.getTvEpgSearchHistory().collectLatest {
                _searchHistory.value = it
            }
        }
        viewModelScope.launch {
            settingsRepository.getUseSearchHighlighting().collectLatest {
                _useSearchHighlighting.value = it
            }
        }
    }

    suspend fun updateLoadingState(isForcedUpdate: Boolean) {
        loadingRepository.updateLoadingState(isForcedUpdate)
    }

    fun fetchData() {
        fetchJob?.cancel()
        _epgBatchSet.value = EventBatchSet()
        _bouquets.value = emptyList()
        fetchJob = viewModelScope.launch {
            _bouquets.value = apiRepository.fetchBouquets(ApiType.TV)
            if (_bouquets.value.isNotEmpty()) {
                if (_currentBouquet.value.isBlank()) {
                    _currentBouquet.value = _bouquets.value[0][0]
                } else if (_bouquets.value.find { it[0] == _currentBouquet.value } == null) {
                    _currentBouquet.value = _bouquets.value[0][0]
                }
                _epgBatchSet.value = apiRepository.fetchEpgEventBatchSet(_currentBouquet.value)
            }
        }
    }

    fun addTimerForEvent(event: Event) {
        viewModelScope.launch {
            apiRepository.addTimerForEvent(
                event.serviceReference, event.id
            )
        }
    }

    fun setCurrentBouquet(bouquetReference: String) {
        _currentBouquet.value = bouquetReference
        fetchData()
    }

    fun updateSearchInput() {
        _searchInput.value = searchFieldState.text.toString()
    }
}