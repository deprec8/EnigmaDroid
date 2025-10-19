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

package io.github.deprec8.enigmadroid.ui.radio

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
import io.github.deprec8.enigmadroid.model.api.Event
import io.github.deprec8.enigmadroid.model.api.EventList
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
class RadioViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val loadingRepository: LoadingRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val searchFieldState = TextFieldState()

    private val _filteredEvents = MutableStateFlow<List<Event>?>(null)
    val filteredEvents: StateFlow<List<Event>?> = _filteredEvents.asStateFlow()

    private val _allEvents = MutableStateFlow<List<EventList>>(emptyList())
    val allEvents: StateFlow<List<EventList>> = _allEvents.asStateFlow()

    private val _loadingState = MutableStateFlow(LoadingState.LOADING)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    private val _searchInput = MutableStateFlow("")
    val searchInput: StateFlow<String> = _searchInput.asStateFlow()

    private val _useSearchHighlighting = MutableStateFlow(true)
    val useSearchHighlighting: StateFlow<Boolean> = _useSearchHighlighting.asStateFlow()

    private val currentBouquetIndex = MutableStateFlow(0)

    private var fetchJob: Job? = null

    init {
        viewModelScope.launch {
            loadingRepository.getLoadingState().collectLatest { state ->
                _loadingState.value = state
            }
        }
        viewModelScope.launch {
            combine(
                _allEvents,
                _searchInput,
                currentBouquetIndex
            ) { allEvents, input, currentBouquetIndex ->
                if (input != "") {
                    searchHistoryRepository.addToRadioSearchHistory(input)
                    FilterUtils.filterEvents(input, allEvents[currentBouquetIndex].events)
                } else {
                    null
                }
            }.collectLatest {
                _filteredEvents.value = it
            }
        }
        viewModelScope.launch {
            searchHistoryRepository.getRadioSearchHistory().collectLatest {
                _searchHistory.value = it
            }
        }
        viewModelScope.launch {
            settingsRepository.getUseSearchHighlighting().collectLatest {
                _useSearchHighlighting.value = it
            }
        }
    }

    suspend fun updateLoadingState(forceUpdate: Boolean) {
        loadingRepository.updateLoadingState(forceUpdate)
    }

    suspend fun buildStreamUrl(sRef: String): String {
        return apiRepository.buildLiveStreamURL(sRef)
    }

    fun fetchData() {
        fetchJob?.cancel()
        _allEvents.value = emptyList()
        fetchJob = viewModelScope.launch {
            apiRepository.fetchEvents(ApiType.RADIO)
                .collect { events ->
                    _allEvents.value += events
                }
        }
    }

    fun play(sRef: String) {
        viewModelScope.launch {
            apiRepository.play(sRef)
        }
    }

    suspend fun addTimer(event: Event) {
        apiRepository.addTimerForEvent(
            event.serviceReference, event.id
        )
    }

    fun updateSearchInput(index: Int) {
        currentBouquetIndex.value = index
        _searchInput.value = searchFieldState.text.toString()
    }


}