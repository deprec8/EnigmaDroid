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
import io.github.deprec8.enigmadroid.model.api.Bouquet
import io.github.deprec8.enigmadroid.model.api.events.Event
import io.github.deprec8.enigmadroid.model.api.events.EventBatchSet
import io.github.deprec8.enigmadroid.ui.components.search.asHighlightedWords
import io.github.deprec8.enigmadroid.utils.FilterUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvEpgViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val loadingRepository: LoadingRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _eventBatchSet = MutableStateFlow<EventBatchSet?>(null)
    val eventBatchSet: StateFlow<EventBatchSet?> = _eventBatchSet.asStateFlow()

    private val _filteredEvents = MutableStateFlow<List<Event>?>(null)
    val filteredEvents: StateFlow<List<Event>?> = _filteredEvents.asStateFlow()

    private val _loadingState = MutableStateFlow(LoadingState.LOADING)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    private val _currentBouquetReference = MutableStateFlow("")
    val currentBouquetReference: StateFlow<String> = _currentBouquetReference.asStateFlow()

    private val _bouquets = MutableStateFlow<List<Bouquet>?>(null)
    val bouquets: StateFlow<List<Bouquet>?> = _bouquets.asStateFlow()

    val searchFieldState = TextFieldState()

    private val searchInput = MutableStateFlow("")
    private val useSearchHighlighting = MutableStateFlow(true)

    val highlightedWords: StateFlow<List<String>> =
        searchInput.asHighlightedWords(useSearchHighlighting).stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var fetchJob: Job? = null

    private var fetchedEventBatchSetMap = emptyMap<String, EventBatchSet>()

    init {
        viewModelScope.launch {
            loadingRepository.getLoadingState().collectLatest { state ->
                _loadingState.value = state
            }
        }
        viewModelScope.launch {
            combine(_eventBatchSet, searchInput) { eventBatchSet, searchInput ->
                if (searchInput.isNotBlank() && eventBatchSet?.eventBatches?.isNotEmpty() == true) {
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
                useSearchHighlighting.value = it
            }
        }
    }

    suspend fun updateLoadingState(isForcedUpdate: Boolean) {
        loadingRepository.updateLoadingState(isForcedUpdate)
    }

    fun fetchData() {
        fetchJob?.cancel()
        _eventBatchSet.value = null
        _bouquets.value = null
        fetchedEventBatchSetMap = emptyMap()
        fetchJob = viewModelScope.launch {
            fetchBouquets()
            fetchEpgBatchSet()
        }
    }

    private suspend fun fetchBouquets() {
        val bouquets = apiRepository.fetchBouquets(ApiType.TV)
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

    fun updateSearchInput() {
        searchInput.value = searchFieldState.text.toString()
    }
}