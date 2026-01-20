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

package io.github.deprec8.enigmadroid.ui.epg.radioEPG

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
import io.github.deprec8.enigmadroid.model.api.EventListList
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
class RadioEPGViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val loadingRepository: LoadingRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val searchFieldState = TextFieldState()

    private val _epgs = MutableStateFlow(EventListList())
    val epgs: StateFlow<EventListList> = _epgs.asStateFlow()

    private val _filteredEPGEvents = MutableStateFlow<List<Event>?>(null)
    val filteredEPGEvents: StateFlow<List<Event>?> = _filteredEPGEvents.asStateFlow()

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

    private var fetchJob: Job? = null

    init {
        viewModelScope.launch {
            loadingRepository.getLoadingState().collectLatest { state ->
                _loadingState.value = state
            }
        }
        viewModelScope.launch {
            combine(_epgs, _searchInput) { epgs, input ->
                if (input != "" && epgs.eventLists.isNotEmpty()) {
                    searchHistoryRepository.addToRadioEPGSearchHistory(input)
                    FilterUtils.filterEvents(input, epgs.eventLists.flatMap { it.events })
                } else {
                    null
                }
            }.collectLatest {
                _filteredEPGEvents.value = it
            }
        }
        viewModelScope.launch {
            searchHistoryRepository.getRadioEPGSearchHistory().collectLatest {
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

    fun fetchData() {
        fetchJob?.cancel()
        _epgs.value = EventListList()
        _bouquets.value = emptyList()
        fetchJob = viewModelScope.launch {
            _bouquets.value = apiRepository.fetchBouquets(ApiType.RADIO)
            if (_bouquets.value.isNotEmpty()) {
                if (_currentBouquet.value == "") {
                    _currentBouquet.value = _bouquets.value[0][0]
                } else if (_bouquets.value.find { it[0] == _currentBouquet.value } == null) {
                    _currentBouquet.value = _bouquets.value[0][0]
                }
                _epgs.value =
                    apiRepository.fetchEpgEvents(_currentBouquet.value)
            }
        }
    }

    fun addTimer(event: Event) {
        viewModelScope.launch {
            apiRepository.addTimerForEvent(
                event.serviceReference, event.id
            )
        }
    }

    fun setCurrentBouquet(bRef: String) {
        _currentBouquet.value = bRef
    }

    fun updateSearchInput() {
        _searchInput.value = searchFieldState.text.toString()
    }
}